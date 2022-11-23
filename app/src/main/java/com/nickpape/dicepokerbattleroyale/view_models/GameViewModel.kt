package com.nickpape.dicepokerbattleroyale.view_models

import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.adapters.PlayerScore
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.models.DiceRoll
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet
import java.util.*
import kotlin.collections.HashMap

class GameViewModel : ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    private var _playerScoreSheet: MediatorLiveData<ScoreSheet>? = null
    fun playerScoreSheet(): LiveData<ScoreSheet> {
        if (_playerScoreSheet == null) {
            val result = MediatorLiveData<ScoreSheet>()
            result.addSource(_selectedPlayer) {
                Log.d(javaClass.simpleName, "Selecting player $it scoresheet from ${playerScoreSheet().value}")
                result.postValue(playerScoreSheets().value?.get(it))
            }

            result.addSource(playerScoreSheets()) {
                Log.d(javaClass.simpleName, "Selecting player ${_selectedPlayer.value} scoresheet from $it")
                result.postValue(it?.get(_selectedPlayer.value))
            }
            _playerScoreSheet = result
        }
        return _playerScoreSheet!!
    }

    private var _playerScoreSheets: MediatorLiveData<HashMap<String, ScoreSheet>>? = null
    fun playerScoreSheets(): LiveData<HashMap<String, ScoreSheet>> {
        if (_playerScoreSheets == null) {
            val result = MediatorLiveData<HashMap<String, ScoreSheet>>()

            result.addSource(_gameId) {
                dbHelp.fetchAllScoreSheets(it, result)
            }

            _playerScoreSheets = result
        }
        return _playerScoreSheets!!
    }

    private var _playerScores: LiveData<List<PlayerScore>>? = null
    fun playerScores(): LiveData<List<PlayerScore>> {
        if (_playerScores == null) {
            val result = MediatorLiveData<List<PlayerScore>>()

            result.addSource(playerScoreSheets()) { scoresheets ->
                if (scoresheets != null) {
                    result.postValue(scoresheets.entries.map { scoresheet ->
                        Log.d(javaClass.simpleName,"Updating player ${scoresheet.key} to ${scoresheet.value.getScore()}")
                        return@map PlayerScore(
                            scoresheet.key,
                            scoresheet.value.getScore(),
                            scoresheet.key == selectedPlayer().value
                        )
                    })
                }
            }

            _playerScores = result
        }
        return _playerScores!!
    }

    private val _selectedPlayer: MutableLiveData<String> = MutableLiveData()
    fun selectedPlayer(): LiveData<String> {
        return _selectedPlayer
    }

    fun selectPlayer(playerId: String) {
        _selectedPlayer.postValue(playerId)
    }



    val _gameId: MutableLiveData<String> = MutableLiveData()
    fun setGameId(gameId: String) {
        _gameId.postValue(gameId)
    }
    fun gameId(): LiveData<String> {
        return _gameId
    }


    fun isGameOver(): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()
        result.value = false

        result.addSource(playerScoreSheet()) { value ->
            if (value == null) {
                result.postValue(false)
                return@addSource
            }

            val isDone = value.ones != null &&
                    value.twos != null &&
                    value.threes != null &&
                    value.fours != null &&
                    value.fives != null &&
                    value.sixes != null &&
                    value.threeOfKind != null &&
                    value.fourOfKind != null &&
                    value.fullHouse != null &&
                    value.smallStraight != null &&
                    value.largeStraight != null &&
                    value.yahtzee != null &&
                    value.chance != null
            result.postValue(isDone)
        }

        return result
    }

    // TODO() - have this be calculated only after dice rolls
    fun observePotentialScores(): LiveData<ScoreSheet> {
        val result = MediatorLiveData<ScoreSheet>()

        result.addSource(playerDice) { value ->
            result.postValue(ScoreSheet.getPotentialScores(value))
        }

        return result
    }

    private val playerDice = MutableLiveData<List<DiceRoll>>(listOf(
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null)
    ))

    fun resetDice() {
        diceCount.value = 0
        playerDice.value = listOf(
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null),
            DiceRoll(null)
        )
    }

    fun observePlayerDice(): LiveData<List<DiceRoll>> {
        return playerDice
    }

    private val random = Random(System.currentTimeMillis())
    private fun randomDice(): Int {
        return random.nextInt(6) + 1
    }

    fun toggleHoldDice(index: Int) {
        playerDice.value!![index].isHeld = !playerDice.value!![index].isHeld
        playerDice.postValue(playerDice.value)
    }

    private val diceCount: MutableLiveData<Int> = MutableLiveData(0)
    fun observeDiceCount(): LiveData<Int> {
        return diceCount
    }

    fun canRollAgain(): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()

        result.addSource(diceCount) {
            result.postValue(it < 3 && isGameOver().value == false)
        }

        result.addSource(isGameOver()) {
            result.postValue(diceCount.value!! < 3 && !it)
        }

        return result
    }

    fun rollDice() {
        val newDice = playerDice.value!!.map {
            if (it.isHeld) {
                return@map it
            }

            return@map DiceRoll(randomDice())
        }

        playerDice.value = newDice
        diceCount.value = diceCount.value?.plus(1)
    }

    fun updateScoresheet() {
        dbHelp.updateScoreSheet(_gameId.value!!, _playerScoreSheet!!.value!!) {

            _playerScoreSheets!!.value = _playerScoreSheets!!.value?.clone() as HashMap<String, ScoreSheet>
            _playerScoreSheet!!.value = _playerScoreSheet!!.value
            _gameId.value = _gameId.value
        }
    }
}
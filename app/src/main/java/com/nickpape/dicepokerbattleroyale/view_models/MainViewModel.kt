package com.nickpape.dicepokerbattleroyale.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.adapters.PlayerScore
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class MainViewModel: ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    // ================= User Authentication =====================
    var firebaseAuthLiveData = FirestoreAuthLiveData()
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }
    // ===========================================================


    // ====================== Games ==============================
    private var _games = MutableLiveData<List<Game>>()

    fun fetchAllGames() {
        dbHelp.fetchAllGames(_games)
    }

    fun games(): LiveData<List<Game>> {
        return _games
    }

    // Get a note from the memory cache
    fun getGame(position: Int) : Game {
        val game = _games.value?.get(position)
        return game!!
    }

    private var _newGame = MutableLiveData<String>()

    fun createGame(playerIds: Set<String>) {
        dbHelp.createNewGame(_newGame, playerIds)
    }

    fun getNewGame(): LiveData<String> {
        return _newGame
    }
    // ===========================================================

    // =================== Players ==========================
    data class PlayerSelection(
        val player: Player,
        var isSelected: Boolean = false
    )

    private var _players = MutableLiveData<HashMap<String, Player>>()

    private var _selectedPlayers = MutableLiveData(HashSet<String>())
    fun selectedPlayers(): LiveData<HashSet<String>> {
        return _selectedPlayers
    }

    fun fetchAllPlayers() {
        dbHelp.fetchAllPlayers(_players)
    }

    public fun playersMap(): LiveData<HashMap<String, Player>> {
        return _players
    }

    private var _playerSelections: LiveData<List<PlayerSelection>>? = null
    fun playersList(): LiveData<List<PlayerSelection>> {
        if (_playerSelections == null) {
            val playerSelections = MediatorLiveData<List<PlayerSelection>>()
            playerSelections.addSource(_players) { players ->
                playerSelections.postValue(players.values.map { player ->
                    PlayerSelection(player)
                })
            }

            playerSelections.addSource(_selectedPlayers) { selectedPlayers ->
                if (playerSelections.value !== null) {
                    playerSelections.value!!.forEach {
                        Log.d(javaClass.simpleName,"Is ${it.player.id} selected? ${selectedPlayers.contains(it.player.id)}")

                        it.isSelected = selectedPlayers.contains(it.player.id)
                    }
                    playerSelections.postValue(playerSelections.value!!)
                }
            }

            _playerSelections = playerSelections
        }

        return _playerSelections!!
    }

    fun toggleSelectPlayer(player: Player) {
        val selectedPlayersSet = _selectedPlayers.value!!
        val key = player.id
        if (selectedPlayersSet.contains(key)) {
            selectedPlayersSet.remove(key)
            Log.d(javaClass.simpleName, "Unselected player $key")
        } else {
            selectedPlayersSet.add(key)
            Log.d(javaClass.simpleName, "Selected player $key")
        }
        _selectedPlayers.value = selectedPlayersSet
    }

    // Get a note from the memory cache
    fun getPlayer(position: Int) : PlayerSelection {
        Log.d(javaClass.simpleName, "Getting player at ${position}")

        val player = playersList().value?.get(position)
        return player!!
    }

    fun addOrUpdatePlayer(player: Player) {
        dbHelp.addOrUpdatePlayer(player)
    }
    // ===========================================================



    // ===========================================================
    //                GAME SPECIFIC STUFF
    // ===========================================================
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

    fun getPlayerNameFromId(playerId: String): String {
        val user = firebaseAuthLiveData.getCurrentUser()
        if (user == null) {
            return ""
        }

        return if (playerId == user.uid) {
            "You"
        } else {
            playersMap().value!![playerId]!!.display_name
        }
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
                            getPlayerNameFromId(scoresheet.key),
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

            _playerScoreSheets!!.value = _playerScoreSheets!!.value
            _playerScoreSheet!!.value = _playerScoreSheet!!.value
            _gameId.value = _gameId.value
        }
    }

    // ===========================================================
    // ===========================================================
}
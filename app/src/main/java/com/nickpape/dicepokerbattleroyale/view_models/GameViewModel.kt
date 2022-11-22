package com.nickpape.dicepokerbattleroyale.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.models.DiceRoll
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet

class GameViewModel : ViewModel() {
    public val playerScoreSheet = MutableLiveData(ScoreSheet())

    fun isGameOver(): LiveData<Boolean> {
        val result = MediatorLiveData<Boolean>()
        result.value = false

        result.addSource(playerScoreSheet) { value ->
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

    private fun randomDice(): Int {
        return (1..6).random()
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
}
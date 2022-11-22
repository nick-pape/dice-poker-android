package com.nickpape.dicepokerbattleroyale.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class DiceRoll(
    public val value: Int?,
    public var isHeld: Boolean = false
)

data class ScoreSheet(
    public var ones: Int? = null,
    public var twos: Int? = null,
    public var threes: Int? = null,
    public var fours: Int? = null,
    public var fives: Int? = null,
    public var sixes: Int? = null,

    public var threeOfKind: Int? = null,
    public var fourOfKind: Int? = null,
    public var fullHouse: Int? = null,
    public var smallStraight: Int? = null,
    public var largeStraight: Int? = null,
    public var yahtzee: Int? = null,
    public var chance: Int? = null
) {
    companion object {
        private fun getUpperScore(dice: List<Int>, value: Int): Int {
            return dice.filter { it == value }.sum()
        }

        private fun checkOfAKind(dice: List<Int>, value: Int, minimum: Int): Int {
            val filtered = dice.filter {
                it == value
            }
            return if (filtered.size >= minimum) {
                dice.sum()
            } else {
                0
            }
        }

        fun getPotentialScores(dice: List<DiceRoll>): ScoreSheet {
            val diceValues = dice.map {
                return@map it.value ?: 0
            }.sorted()

            val potentialScores = ScoreSheet()

            potentialScores.ones   = getUpperScore(diceValues, 1)
            potentialScores.twos   = getUpperScore(diceValues, 2)
            potentialScores.threes = getUpperScore(diceValues, 3)
            potentialScores.fours  = getUpperScore(diceValues, 4)
            potentialScores.fives  = getUpperScore(diceValues, 5)
            potentialScores.sixes  = getUpperScore(diceValues, 6)

            potentialScores.threeOfKind = (1..6).map {
                return@map checkOfAKind(diceValues, it, 3)
            }.max()

            potentialScores.fourOfKind = (1..6).map {
                return@map checkOfAKind(diceValues, it, 4)
            }.max()

            // check full house, this one is tougher
            // get counts for each dice, check if we have one with 3 and one with 2
            val counts = (1..6).map { searchValue ->
                diceValues.filter { diceValue -> searchValue == diceValue }.size
            }
            val diceWithThree = counts.indexOf(3) + 1
            val diceWithTwo = counts.indexOf(2) + 1

            potentialScores.fullHouse = if (diceWithThree != 0 && diceWithTwo != 0) {
                25
            } else {
                0
            }

            // yahtzee is simple
            val diceWithFive = counts.indexOf(5) + 1
            potentialScores.yahtzee = if (diceWithFive != 0) {
                50
            } else {
                0
            }

            // small and large straight are easy
            potentialScores.smallStraight = if (
                diceValues.containsAll(listOf(1, 2, 3, 4)) ||
                diceValues.containsAll(listOf(2, 3, 4, 5)) ||
                diceValues.containsAll(listOf(3, 4, 5, 6))
            ) {
                30
            } else {
                0
            }

            potentialScores.largeStraight = if (
                diceValues.containsAll(listOf(1, 2, 3, 4, 5)) ||
                diceValues.containsAll(listOf(2, 3, 4, 5, 6))
            ) {
                40
            } else {
                0
            }

            potentialScores.chance = diceValues.sum()

            return potentialScores
        }
    }
}

class CreateGameViewModel : ViewModel() {
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
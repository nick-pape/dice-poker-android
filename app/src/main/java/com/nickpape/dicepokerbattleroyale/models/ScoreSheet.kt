package com.nickpape.dicepokerbattleroyale.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ScoreSheet(
    var ones: Int? = null,
    var twos: Int? = null,
    var threes: Int? = null,
    var fours: Int? = null,
    var fives: Int? = null,
    var sixes: Int? = null,

    var threeOfKind: Int? = null,
    var fourOfKind: Int? = null,
    var fullHouse: Int? = null,
    var smallStraight: Int? = null,
    var largeStraight: Int? = null,
    var yahtzee: Int? = null,
    var chance: Int? = null,
    @DocumentId var id: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null
) {
    fun getUpperScore(): Int {
        val rawUpper = (ones ?: 0) +
                (twos ?: 0) +
                (threes ?: 0) +
                (fours ?: 0) +
                (fives ?: 0) +
                (sixes ?: 0)
        return rawUpper + if (rawUpper >= 63) { 35 } else { 0 }
    }

    fun getLowerScore(): Int {
        return (threeOfKind ?: 0) +
            (fourOfKind ?: 0) +
            (fullHouse ?: 0) +
            (smallStraight ?: 0) +
            (largeStraight ?: 0) +
            (yahtzee ?: 0) +
            (chance ?: 0)
    }

    fun getScore(): Int {
        return getUpperScore() + getLowerScore()
    }

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
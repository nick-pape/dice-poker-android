package com.nickpape.dicepokerbattleroyale.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.Exclude

enum class ScoreableField {
    Ones,
    Twos,
    Threes,
    Fours,
    Fives,
    Sixes,
    ThreeOfAKind,
    FourOfAKind,
    FullHouse,
    SmallStraight,
    LargeStraight,
    Yahtzee,
    Chance
}

class ScoreSheet {
    var id: String = ""
    var timeStamp: Timestamp? = null

    private constructor() {

    }

    private var fieldsMap = HashMap<ScoreableField, Int?>()

    fun getField(field: ScoreableField): Int? {
        return fieldsMap[field]
    }

    fun getFieldScore(field: ScoreableField): Int {
        return fieldsMap[field] ?: 0
    }

    fun setField(field: ScoreableField, value: Int?) {
        fieldsMap[field] = value
    }

    companion object {
        fun fromRawScoreSheet(rawScoreSheet: RawScoreSheet): ScoreSheet {
            val scoresheet = ScoreSheet()
            scoresheet.setField(ScoreableField.Ones, rawScoreSheet.ones)
            scoresheet.setField(ScoreableField.Twos, rawScoreSheet.twos)
            scoresheet.setField(ScoreableField.Threes, rawScoreSheet.threes)
            scoresheet.setField(ScoreableField.Fours, rawScoreSheet.fours)
            scoresheet.setField(ScoreableField.Fives, rawScoreSheet.fives)
            scoresheet.setField(ScoreableField.Sixes, rawScoreSheet.sixes)

            scoresheet.setField(ScoreableField.ThreeOfAKind, rawScoreSheet.threeOfKind)
            scoresheet.setField(ScoreableField.FourOfAKind, rawScoreSheet.fourOfKind)
            scoresheet.setField(ScoreableField.FullHouse, rawScoreSheet.fullHouse)
            scoresheet.setField(ScoreableField.SmallStraight, rawScoreSheet.smallStraight)
            scoresheet.setField(ScoreableField.LargeStraight, rawScoreSheet.largeStraight)
            scoresheet.setField(ScoreableField.Yahtzee, rawScoreSheet.yahtzee)
            scoresheet.setField(ScoreableField.Chance, rawScoreSheet.chance)

            scoresheet.id = rawScoreSheet.id
            scoresheet.timeStamp = rawScoreSheet.timeStamp
            return scoresheet
        }
    }

    fun toRawScoreSheet(): RawScoreSheet {
        val scoresheet = RawScoreSheet()
        scoresheet.ones = getField(ScoreableField.Ones)
        scoresheet.twos = getField(ScoreableField.Twos)
        scoresheet.threes = getField(ScoreableField.Threes)
        scoresheet.fours = getField(ScoreableField.Fours)
        scoresheet.fives = getField(ScoreableField.Fives)
        scoresheet.sixes = getField(ScoreableField.Sixes)

        scoresheet.threeOfKind = getField(ScoreableField.ThreeOfAKind)
        scoresheet.fourOfKind = getField(ScoreableField.FourOfAKind)
        scoresheet.fullHouse = getField(ScoreableField.FullHouse)
        scoresheet.smallStraight = getField(ScoreableField.SmallStraight)
        scoresheet.largeStraight = getField(ScoreableField.LargeStraight)
        scoresheet.yahtzee = getField(ScoreableField.Yahtzee)
        scoresheet.chance = getField(ScoreableField.Chance)

        scoresheet.id = id
        scoresheet.timeStamp = timeStamp

        return scoresheet
    }

    private fun getRawUpperScore(): Int {
        return getFieldScore(ScoreableField.Ones) +
                getFieldScore(ScoreableField.Twos) +
                getFieldScore(ScoreableField.Threes) +
                getFieldScore(ScoreableField.Fours) +
                getFieldScore(ScoreableField.Fives) +
                getFieldScore(ScoreableField.Sixes)
    }

    fun isGameOver(): Boolean {
        return ScoreableField.values()
            .map { getField(it) != null }
            .all { it }
    }

    private fun hasBonus(): Boolean {
        return getRawUpperScore() >= 63
    }

    fun getUpperScore(): Int {
        val rawUpper = getRawUpperScore()
        return rawUpper + if (hasBonus()) { 35 } else { 0 }
    }

    fun getLowerScore(): Int {
        return getFieldScore(ScoreableField.ThreeOfAKind) +
                getFieldScore(ScoreableField.FourOfAKind) +
                getFieldScore(ScoreableField.FullHouse) +
                getFieldScore(ScoreableField.SmallStraight) +
                getFieldScore(ScoreableField.LargeStraight) +
                getFieldScore(ScoreableField.Yahtzee) +
                getFieldScore(ScoreableField.Chance)
    }

    fun getScore(): Int {
        return getUpperScore() + getLowerScore()
    }
}

data class RawScoreSheet(
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
    @ServerTimestamp var timeStamp: Timestamp? = null
) {

    @Exclude fun toScoreSheet(): ScoreSheet {
        return ScoreSheet.fromRawScoreSheet(this)
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

        fun getPotentialScores(dice: List<DiceRoll>): RawScoreSheet {
            val diceValues = dice.map {
                return@map it.value ?: 0
            }.sorted()

            val potentialScores = RawScoreSheet()

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
package com.nickpape.dicepokerbattleroyale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class DiceRoll(
    public val value: Int?,
    public var isHeld: Boolean = false
)

class CreateGameViewModel : ViewModel() {
    private val playerDice = MutableLiveData<List<DiceRoll>>(listOf(
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null),
        DiceRoll(null)
    ))

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

    fun rollDice() {
        val newDice = playerDice.value!!.map {
            if (it.isHeld) {
                return@map it
            }

            return@map DiceRoll(randomDice())
        }

        playerDice.value = newDice
    }
}
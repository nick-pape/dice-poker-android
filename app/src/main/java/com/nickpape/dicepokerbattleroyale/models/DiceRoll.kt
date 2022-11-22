package com.nickpape.dicepokerbattleroyale.models

data class DiceRoll(
    public val value: Int?,
    public var isHeld: Boolean = false
)
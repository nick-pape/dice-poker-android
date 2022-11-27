package com.nickpape.dicepokerbattleroyale.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.Exclude
import java.util.*

data class Game(
    // Written on the server
    @ServerTimestamp val createdTimeStamp: Timestamp? = null,
    var updatedTimeStamp: Timestamp? = Timestamp.now(),
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = "",
    var playerIds: List<String> = listOf(),
    var currentPlayerIndex: Int = 0,
    var speedMode: Boolean = false
) {
    fun nextPlayer() {
        if (currentPlayerIndex >= playerIds.size - 1) {
            currentPlayerIndex = 0
        } else {
            currentPlayerIndex += 1
        }
    }
}
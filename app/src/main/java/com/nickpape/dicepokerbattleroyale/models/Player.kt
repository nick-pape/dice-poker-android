package com.nickpape.dicepokerbattleroyale.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Player(
    var display_name: String = "",
    @DocumentId var id: String = "",
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null
)
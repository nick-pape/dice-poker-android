package com.nickpape.dicepokerbattleroyale.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.nickpape.dicepokerbattleroyale.models.Game

class ViewModelDBHelper {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val gameCollection = "allGames"

    fun fetchAllGames(gamesList: MutableLiveData<List<Game>>) {
        db.collection(gameCollection)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allGames fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                gamesList.postValue(result.documents.mapNotNull {
                    it.toObject(Game::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allGames fetch FAILED ", it)
            }
    }
}
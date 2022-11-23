package com.nickpape.dicepokerbattleroyale.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.models.Player
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet

class ViewModelDBHelper {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val gameCollection = "allGames"
    private val playerCollection = "allPlayers"
    private val scoresheetsCollection = "scoresheets"

    fun fetchAllScoreSheets(gameId: String, scoresheets: MutableLiveData<HashMap<String, ScoreSheet>>) {
        db.collection(gameCollection).document(gameId).collection(scoresheetsCollection)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Fetched scoresheets for $gameId")

                val resultMap = HashMap<String, ScoreSheet>()
                result.documents.forEach {
                    val scoresheet = it.toObject(ScoreSheet::class.java)!!
                    resultMap[scoresheet.id] = scoresheet
                }

                scoresheets.postValue(resultMap)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED fetch scoresheets for $gameId")
            }
    }

    fun updateScoreSheet(gameId: String, scoresheet: ScoreSheet, onSuccessListener: OnSuccessListener<Void>) {
        db.collection(gameCollection)
            .document(gameId)
            .collection(scoresheetsCollection)
            .document(scoresheet.id)
            .set(scoresheet)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Updated scoresheet ${scoresheet.id} for game $gameId")
                onSuccessListener.onSuccess(it)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED update scoresheet ${scoresheet.id} for game $gameId")
            }
    }

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

    fun fetchAllPlayers(playersList: MutableLiveData<List<Player>>) {
        db.collection(playerCollection)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allPlayers fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                playersList.postValue(result.documents.mapNotNull {
                    it.toObject(Player::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allPlayers fetch FAILED ", it)
            }
    }

    fun addOrUpdatePlayer(player: Player) {
        db.collection(playerCollection).document(player.id).set(player)
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "added player ${player.id} ${player.display_name}")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED to add player ${player.id} ${player.display_name}")
            }
    }

    fun createNewGame(newGame: MutableLiveData<String>, playerIds: Set<String>) {
        val gameRef = db.collection(gameCollection).document()
        val collectionRef = gameRef.collection(scoresheetsCollection)

        db.runTransaction { batch ->
            batch.set(gameRef, Game())
            val scoresheetRefs = playerIds.forEach { playerId ->
                val scoresheetRef = collectionRef.document(playerId)
                batch.set(scoresheetRef, ScoreSheet())
            }
        }.addOnCompleteListener {
            Log.d(javaClass.simpleName, "Created game ${gameRef.id}")
            newGame.postValue(gameRef.id)
        }.addOnFailureListener {
            Log.d(javaClass.simpleName, "FAILED create game ${gameRef.id}")
        }
    }
}
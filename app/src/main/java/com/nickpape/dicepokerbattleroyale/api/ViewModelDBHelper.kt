package com.nickpape.dicepokerbattleroyale.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.models.Player
import com.nickpape.dicepokerbattleroyale.models.RawScoreSheet
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet

class ViewModelDBHelper {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val gameCollection = "allGames"
    private val playerCollection = "allPlayers"
    private val scoresheetsCollection = "scoresheets"

    fun fetchAllScoreSheetsForUser(scoresheetsLiveData: MutableLiveData<List<ScoreSheet>>, userId: String, games: List<Game>) {
        val scoresheets = ArrayList<ScoreSheet>()
        db.runTransaction { batch ->
            for (game in games) {
                val res = batch.get(
                    db.collection(gameCollection)
                        .document(game.firestoreID)
                        .collection(scoresheetsCollection)
                        .document(userId)
                )

                val scoresheet = res.toObject(RawScoreSheet::class.java)

                if (scoresheet != null) {
                    scoresheets.add(scoresheet.toScoreSheet())
                }
            }
        }.addOnSuccessListener {
            Log.d(javaClass.simpleName, "Loaded ${scoresheets.size} scoresheets for player $userId")
            scoresheetsLiveData.postValue(scoresheets)
        }.addOnFailureListener {
            Log.d(javaClass.simpleName, "Failed to load all scoresheets for user ${userId}")
        }
    }

    fun refreshGame(gameData: MutableLiveData<Game>) {
        val game = gameData.value!!
        db.collection(gameCollection).document(game.firestoreID)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Refreshed Game for ${game.firestoreID}")

                gameData.postValue(
                    result.toObject(Game::class.java)!!
                )
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED refresh game for ${game.firestoreID}")
            }
    }

    fun fetchAllScoreSheets(game: Game, scoresheets: MutableLiveData<HashMap<String, ScoreSheet>>, onSuccessListener: OnSuccessListener<Void>) {
        db.collection(gameCollection).document(game.firestoreID).collection(scoresheetsCollection)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Fetched scoresheets for ${game.firestoreID}")

                val resultMap = HashMap<String, ScoreSheet>()
                result.documents.forEach {
                    val rawScoresheet = it.toObject(RawScoreSheet::class.java)!!
                    resultMap[rawScoresheet.id] = rawScoresheet.toScoreSheet()
                }

                scoresheets.postValue(resultMap)
                onSuccessListener.onSuccess(null)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED fetch scoresheets for ${game.firestoreID}")
            }
    }

    fun updateScoreSheetAndGame(game: Game, scoresheet: ScoreSheet, onSuccess: () -> Unit) {
        val gameRef = db.collection(gameCollection).document(game.firestoreID)
        val scoresheetRef = gameRef.collection(scoresheetsCollection).document(scoresheet.id)

        game.updatedTimeStamp = Timestamp.now()

        db.runTransaction { batch ->
            batch.set(gameRef, game)
            batch.set(scoresheetRef, scoresheet.toRawScoreSheet())
        }.addOnSuccessListener {
            Log.d(javaClass.simpleName, "Updated scoresheet ${scoresheet.id} for game ${game.firestoreID}")
            onSuccess()
        }.addOnFailureListener {
            Log.d(javaClass.simpleName, "FAILED update scoresheet ${scoresheet.id} for game ${game.firestoreID}")
        }
    }

    fun fetchAllGames(gamesList: MutableLiveData<MutableList<Game>>, onSuccess: () -> Unit) {
        db.collection(gameCollection)
            .orderBy("updatedTimeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allGames fetch ${result!!.documents.size}")
                // NB: This is done on a background thread

                gamesList.postValue(ArrayList(result.documents.mapNotNull {
                    it.toObject(Game::class.java)
                }))

                onSuccess()
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allGames fetch FAILED ", it)
            }
    }

    fun fetchAllPlayers(playersList: MutableLiveData<HashMap<String, Player>>, onSuccess: () -> Unit) {
        db.collection(playerCollection)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allPlayers fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                val playerMap = HashMap<String, Player>()
                result.documents.forEach {
                    val player = it.toObject(Player::class.java)!!
                    playerMap[player.id] = player
                }
                playersList.postValue(playerMap)
                onSuccess()
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

    fun createNewGame(playerIds: List<String>, speedMode: Boolean, onSuccess: (game: Game) -> Unit) {
        val gameRef = db.collection(gameCollection).document()
        val collectionRef = gameRef.collection(scoresheetsCollection)

        db.runTransaction { batch ->
            batch.set(gameRef, Game(playerIds = playerIds, speedMode = speedMode))
            playerIds.forEach { playerId ->
                batch.set(collectionRef.document(playerId), RawScoreSheet())
            }
        }.addOnSuccessListener {
            Log.d(javaClass.simpleName, "Created game ${gameRef.id}")

            gameRef.get().addOnSuccessListener {
                val game = it.toObject(Game::class.java)!!
                onSuccess(game)
            }.addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED reading newly created game ${gameRef.id}: $it")
            }
        }.addOnFailureListener {
            Log.d(javaClass.simpleName, "FAILED create game ${gameRef.id}: $it")
        }
    }
}
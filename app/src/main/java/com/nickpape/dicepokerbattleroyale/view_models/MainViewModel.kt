package com.nickpape.dicepokerbattleroyale.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.models.GameWithPlayers
import com.nickpape.dicepokerbattleroyale.models.Player
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet

class MainViewModel: ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    // ================= User Authentication =====================
    var firebaseAuthLiveData = FirestoreAuthLiveData()
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }
    // ===========================================================


    // ====================== Games ==============================
    private var _games = MutableLiveData<List<Game>>()

    fun fetchAllGames() {
        dbHelp.fetchAllGames(_games)
    }

    fun games(): LiveData<List<Game>> {
        return _games
    }

    // Get a note from the memory cache
    fun getGame(position: Int) : Game {
        val game = _games.value?.get(position)
        return game!!
    }

    private var _newGame = MutableLiveData<String>()

    fun createGame(playerIds: Set<String>) {
        dbHelp.createNewGame(_newGame, playerIds)
    }

    fun getNewGame(): LiveData<String> {
        return _newGame
    }
    // ===========================================================

    // =================== Players ==========================
    data class PlayerSelection(
        val player: Player,
        var isSelected: Boolean = false
    )

    private var _players = MutableLiveData<List<Player>>()

    private var _selectedPlayers = MutableLiveData(HashSet<String>())
    fun selectedPlayers(): LiveData<HashSet<String>> {
        return _selectedPlayers
    }

    fun fetchAllPlayers() {
        dbHelp.fetchAllPlayers(_players)
    }

    private var _playerSelections: LiveData<List<PlayerSelection>>? = null
    fun players(): LiveData<List<PlayerSelection>> {
        if (_playerSelections == null) {
            val playerSelections = MediatorLiveData<List<PlayerSelection>>()
            playerSelections.addSource(_players) { players ->
                playerSelections.postValue(players.map { player ->
                    PlayerSelection(player)
                })
            }

            playerSelections.addSource(_selectedPlayers) { selectedPlayers ->
                if (playerSelections.value !== null) {
                    playerSelections.value!!.forEach {
                        Log.d(javaClass.simpleName,"Is ${it.player.id} selected? ${selectedPlayers.contains(it.player.id)}")

                        it.isSelected = selectedPlayers.contains(it.player.id)
                    }
                    playerSelections.postValue(playerSelections.value!!)
                }
            }

            _playerSelections = playerSelections
        }

        return _playerSelections!!
    }

    fun toggleSelectPlayer(player: Player) {
        val selectedPlayersSet = _selectedPlayers.value!!
        val key = player.id
        if (selectedPlayersSet.contains(key)) {
            selectedPlayersSet.remove(key)
            Log.d(javaClass.simpleName, "Unselected player $key")
        } else {
            selectedPlayersSet.add(key)
            Log.d(javaClass.simpleName, "Selected player $key")
        }
        _selectedPlayers.value = selectedPlayersSet
    }

    // Get a note from the memory cache
    fun getPlayer(position: Int) : PlayerSelection {
        Log.d(javaClass.simpleName, "Getting player at ${position}")

        val player = players().value?.get(position)
        return player!!
    }

    fun addOrUpdatePlayer(player: Player) {
        dbHelp.addOrUpdatePlayer(player)
    }
    // ===========================================================




    // =================== Score Sheets ==========================
    private var _scoresheets = MutableLiveData<HashMap<String, ScoreSheet>>()
    // ===========================================================
}
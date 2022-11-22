package com.nickpape.dicepokerbattleroyale.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.Game
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
    // ===========================================================

    // =================== Players ==========================
    private var _players = MutableLiveData<List<Player>>()

    private var _selectedPlayers = MutableLiveData<HashSet<String>>()

    fun fetchAllPlayers() {
        dbHelp.fetchAllPlayers(_players)
    }

    fun players(): LiveData<List<Player>> {
        return _players
    }

    fun isSelected(player: Player): Boolean {
        return _selectedPlayers.value!!.contains(player.id)
    }

    fun toggleSelectPlayer(player: Player) {
        val selectedPlayersSet = _selectedPlayers.value!!
        val key = player.id
        if (selectedPlayersSet.contains(key)) {
            selectedPlayersSet.remove(key)
        } else {
            selectedPlayersSet.add(key)
        }
        _selectedPlayers.postValue(selectedPlayersSet)
    }

    fun resetSelectedPlayers() {
        _selectedPlayers.postValue(HashSet<String>())
    }

    // Get a note from the memory cache
    fun getPlayer(position: Int) : Player {
        val player = _players.value?.get(position)
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
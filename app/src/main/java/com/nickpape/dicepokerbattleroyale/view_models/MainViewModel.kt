package com.nickpape.dicepokerbattleroyale.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet

class MainViewModel: ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    // ================= User Authentication =====================
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
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
        val note = _games.value?.get(position)
        return note!!
    }
    // ===========================================================


    // =================== Score Sheets ==========================
    private var _scoresheets = MutableLiveData<HashMap<String, ScoreSheet>>()
    // ===========================================================
}
package com.nickpape.dicepokerbattleroyale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nickpape.dicepokerbattleroyale.api.ViewModelDBHelper
import com.nickpape.dicepokerbattleroyale.auth.FirestoreAuthLiveData
import com.nickpape.dicepokerbattleroyale.models.Game

class MainViewModel: ViewModel() {
    // Database access
    private val dbHelp = ViewModelDBHelper()

    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    private var gamesList = MutableLiveData<List<Game>>()

    fun observeGamesList(): LiveData<List<Game>> {
        return gamesList
    }

    // Get a note from the memory cache
    fun getGame(position: Int) : Game {
        val note = gamesList.value?.get(position)
        return note!!
    }

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }

    fun fetchAllGames() {
        dbHelp.fetchAllGames(gamesList)
    }
}
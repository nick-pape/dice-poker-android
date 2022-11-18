package com.nickpape.dicepokerbattleroyale

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun updateUser() {
        firebaseAuthLiveData.updateUser()
    }
}
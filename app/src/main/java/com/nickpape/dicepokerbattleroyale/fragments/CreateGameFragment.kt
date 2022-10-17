package com.nickpape.dicepokerbattleroyale.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nickpape.dicepokerbattleroyale.CreateGameViewModel
import com.nickpape.dicepokerbattleroyale.R

class CreateGameFragment : Fragment() {

    companion object {
        fun newInstance() = CreateGameFragment()
    }

    private lateinit var viewModel: CreateGameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateGameViewModel::class.java]
        // TODO: Use the ViewModel
    }

}
package com.nickpape.dicepokerbattleroyale.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentHomeBinding
import com.nickpape.dicepokerbattleroyale.databinding.FragmentSettingsBinding
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        viewModel.fetchAllGames()
        viewModel.stats().observe(viewLifecycleOwner) {
            binding.highScore.text = it.highScore.toString()
            binding.lowScore.text = it.lowScore.toString()
            binding.medianScore.text = it.medianScore.toString()
            binding.meanScore.text = it.meanScore.toString()
            binding.numYahtzees.text = it.numYahtzees.toString()
            binding.numGames.text = it.numGames.toString()
        }
        binding.highScore

        return binding.root
    }
}
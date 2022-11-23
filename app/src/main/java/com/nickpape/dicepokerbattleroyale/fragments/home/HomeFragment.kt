package com.nickpape.dicepokerbattleroyale.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.adapters.GameListAdapter
import com.nickpape.dicepokerbattleroyale.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = GameListAdapter(viewLifecycleOwner, viewModel, findNavController())
        binding.gamesList.adapter = adapter
        binding.gamesList.layoutManager = LinearLayoutManager(binding.gamesList.context)
        val itemDecor = DividerItemDecoration(binding.gamesList.context, LinearLayoutManager.VERTICAL)
        binding.gamesList.addItemDecoration(itemDecor)

        viewModel.games().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.fetchAllGames()
        viewModel.fetchAllPlayers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreateGame.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_CreateGameFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
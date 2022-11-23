package com.nickpape.dicepokerbattleroyale.fragments.game

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.adapters.PlayerSelectorAdapter
import com.nickpape.dicepokerbattleroyale.databinding.FragmentCreateGameBinding
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class CreateGameFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)

        val adapter = PlayerSelectorAdapter(viewModel)
        binding.playersList.adapter = adapter
        binding.playersList.layoutManager = LinearLayoutManager(binding.playersList.context)
        val itemDecor = DividerItemDecoration(binding.playersList.context, LinearLayoutManager.VERTICAL)
        binding.playersList.addItemDecoration(itemDecor)

        viewModel.playersList().observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "Updating list: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        viewModel.fetchAllPlayers()

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createGameButton.setOnClickListener {
            viewModel.getNewGame().observe(viewLifecycleOwner) {
                // TODO -- remove this frame from backstack
                val directions = CreateGameFragmentDirections.actionCreateGameFragmentToReviewGameFragment(it)
                findNavController().navigate(directions)
            }

            viewModel.createGame(
                viewModel.selectedPlayers().value!!
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
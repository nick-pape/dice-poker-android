package com.nickpape.dicepokerbattleroyale.fragments.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.adapters.PlayerScore
import com.nickpape.dicepokerbattleroyale.adapters.PlayerChipAdapter
import com.nickpape.dicepokerbattleroyale.databinding.FragmentGameBinding
import com.nickpape.dicepokerbattleroyale.view_models.GameViewModel
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: GameViewModel by activityViewModels()

    private val args by navArgs<GameFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        val adapter = PlayerChipAdapter()
        binding.playerChips.adapter = adapter
        binding.playerChips.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        viewModel.playerScores().observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
            adapter.notifyDataSetChanged()
        }

        viewModel.selectPlayer(
            mainViewModel.firebaseAuthLiveData.getCurrentUser()!!.uid
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
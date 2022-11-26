package com.nickpape.dicepokerbattleroyale.fragments.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.*
import com.nickpape.dicepokerbattleroyale.adapters.PlayerRankAdapter
import com.nickpape.dicepokerbattleroyale.databinding.FragmentReviewGameBinding
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class ReviewGameFragment : Fragment() {
    private val args by navArgs<ReviewGameFragmentArgs>()

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentReviewGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewGameBinding.inflate(inflater, container, false)

        viewModel.setGameById(
            args.gameId
        )

        binding.gameId.text = args.gameId

        binding.buttonContinueGame.setOnClickListener {
            val directions = ReviewGameFragmentDirections.actionReviewGameFragmentToPlayGameFragment(
                args.gameId
            )
            findNavController().navigate(directions)
        }

        val adapter = PlayerRankAdapter()
        binding.playerRankList.adapter = adapter
        binding.playerRankList.layoutManager = LinearLayoutManager(binding.root.context)

        viewModel.playerScores().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.sortedBy { -it.score })
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

}
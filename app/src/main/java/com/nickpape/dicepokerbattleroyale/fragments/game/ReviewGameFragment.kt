package com.nickpape.dicepokerbattleroyale.fragments.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nickpape.dicepokerbattleroyale.*
import com.nickpape.dicepokerbattleroyale.databinding.FragmentReviewGameBinding
import com.nickpape.dicepokerbattleroyale.view_models.GameViewModel
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewGameFragment : Fragment() {
    private val args by navArgs<ReviewGameFragmentArgs>()

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: GameViewModel by activityViewModels()

    private var _binding: FragmentReviewGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewGameBinding.inflate(inflater, container, false)

        viewModel.setGameId(
            args.gameId
        )

        binding.gameId.text = args.gameId

        binding.buttonContinueGame.setOnClickListener {
            val directions = ReviewGameFragmentDirections.actionReviewGameFragmentToPlayGameFragment(
                args.gameId
            )
            findNavController().navigate(directions)
        }



        return binding.root
    }

}
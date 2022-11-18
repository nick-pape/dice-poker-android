package com.nickpape.dicepokerbattleroyale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nickpape.dicepokerbattleroyale.*
import com.nickpape.dicepokerbattleroyale.databinding.FragmentReviewGameBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewGameFragment : Fragment() {
    private val args by navArgs<ReviewGameFragmentArgs>()

    private var _binding: FragmentReviewGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewGameBinding.inflate(inflater, container, false)

        binding.gameId.text = args.gameId

        binding.buttonContinueGame.setOnClickListener {
            val directions = ReviewGameFragmentDirections.actionReviewGameFragmentToPlayGameFragment()
            findNavController().navigate(directions)
        }

        return binding.root
    }

}
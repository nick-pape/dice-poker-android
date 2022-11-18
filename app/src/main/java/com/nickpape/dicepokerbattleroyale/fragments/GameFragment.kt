package com.nickpape.dicepokerbattleroyale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.Player
import com.nickpape.dicepokerbattleroyale.PlayerChipAdapter
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentGameBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameBinding.inflate(inflater, container, false)

        val adapter = PlayerChipAdapter()
        binding.playerChips.adapter = adapter
        binding.playerChips.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        adapter.submitList(listOf(
            Player("Nick", 123),
            Player("Katie", 420),
            Player("Dustin", 69),
            Player("Player 1", 1),
            Player("Player 2", 2),
            Player("Player 3", 3),
            Player("Player 4", 4),
            Player("Player 5", 5),
            Player("Player 6", 6)
        ))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_PlayGameFragment_to_HomeFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
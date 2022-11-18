package com.nickpape.dicepokerbattleroyale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nickpape.dicepokerbattleroyale.MainViewModel
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = GameListAdapter(viewModel)
        binding.gamesList.adapter = adapter
        binding.gamesList.layoutManager = LinearLayoutManager(binding.gamesList.context)
        val itemDecor = DividerItemDecoration(binding.gamesList.context, LinearLayoutManager.VERTICAL)
        binding.gamesList.addItemDecoration(itemDecor)

        viewModel.observeGamesList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.fetchAllGames()

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
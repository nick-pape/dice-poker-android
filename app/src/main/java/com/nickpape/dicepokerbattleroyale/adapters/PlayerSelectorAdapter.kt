package com.nickpape.dicepokerbattleroyale.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerRowBinding
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class PlayerSelectorAdapter(private val viewModel: MainViewModel)
: ListAdapter<MainViewModel.PlayerSelection, PlayerSelectorAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<MainViewModel.PlayerSelection>() {
        override fun areItemsTheSame(oldItem: MainViewModel.PlayerSelection, newItem: MainViewModel.PlayerSelection): Boolean {
            Log.d(javaClass.simpleName, "Are items the same?")
            Log.d(javaClass.simpleName, oldItem.toString())
            Log.d(javaClass.simpleName, newItem.toString())

            return oldItem.player.id == newItem.player.id
                    && oldItem.isSelected == newItem.isSelected
        }

        override fun areContentsTheSame(oldItem: MainViewModel.PlayerSelection, newItem: MainViewModel.PlayerSelection): Boolean {
            Log.d(javaClass.simpleName, "Diffing two players:")
            Log.d(javaClass.simpleName, oldItem.toString())
            Log.d(javaClass.simpleName, newItem.toString())

            return oldItem.player.id == newItem.player.id
                    && oldItem.player.timeStamp == newItem.player.timeStamp
                    && oldItem.player.display_name == newItem.player.display_name
                    && oldItem.isSelected == newItem.isSelected
        }
    }

    inner class VH(private val rowBinding: FragmentPlayerRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val playerSelection = viewModel.getPlayer(position)
            holder.rowBinding.playerName.text = playerSelection.player.display_name
            holder.rowBinding.isSelected.isChecked = playerSelection.isSelected

            holder.rowBinding.root.setOnClickListener {
                viewModel.toggleSelectPlayer(playerSelection.player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = FragmentPlayerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}
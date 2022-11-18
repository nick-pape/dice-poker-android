package com.nickpape.dicepokerbattleroyale.fragments

import com.nickpape.dicepokerbattleroyale.MainViewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.databinding.FragmentGameRowBinding

class GameListAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Game, GameListAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }

    inner class VH(private val rowBinding: FragmentGameRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val game = viewModel.getGame(position)
            holder.rowBinding.playerNames.text = game.players.joinToString()
            // Note to future me: It might be fun to display the date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = FragmentGameRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}
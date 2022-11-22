package com.nickpape.dicepokerbattleroyale.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerRowBinding
import com.nickpape.dicepokerbattleroyale.models.Player
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

class PlayerSelectorAdapter(private val viewModel: MainViewModel)
: ListAdapter<Player, PlayerSelectorAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.timeStamp == newItem.timeStamp
                    && oldItem.display_name == newItem.display_name
        }
    }

    inner class VH(private val rowBinding: FragmentPlayerRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val player = viewModel.getPlayer(position)
            holder.rowBinding.playerName.text = player.display_name

            holder.rowBinding.root.setOnClickListener {
                viewModel.toggleSelectPlayer(player)
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
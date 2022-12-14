package com.nickpape.dicepokerbattleroyale.adapters

import android.util.Log
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nickpape.dicepokerbattleroyale.models.Game
import com.nickpape.dicepokerbattleroyale.databinding.FragmentGameRowBinding
import com.nickpape.dicepokerbattleroyale.fragments.home.HomeFragmentDirections

class GameListAdapter(private val viewLifecycleOwner: LifecycleOwner, private val viewModel: MainViewModel, private val navController: NavController)
    : ListAdapter<Game, GameListAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return false
        }
    }

    inner class VH(private val rowBinding: FragmentGameRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val game = viewModel.getGame(position)

            holder.rowBinding.playerNames.text = game.playerIds.map {
                val name = viewModel.getPlayerNameFromId(it)
                Log.d(javaClass.simpleName, "Mapped $it to $name")
                return@map name
            }.joinToString()

            holder.rowBinding.lastPlayed.text = android.text.format.DateUtils.getRelativeTimeSpanString(game.updatedTimeStamp!!.seconds * 1000)

            holder.rowBinding.root.setOnClickListener {
                val directions = HomeFragmentDirections.actionHomeFragmentToReviewGameFragment(
                    game.firestoreID
                )
                navController.navigate(directions)
            }
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
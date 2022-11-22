package com.nickpape.dicepokerbattleroyale.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerChipBinding

data class Player(val name: String, val score: Int)

class PlayerChipAdapter : ListAdapter<Player, PlayerChipAdapter.VH>(PlayerDiff()) {

    // ViewHolder pattern
    inner class VH(val playerChipBinding: FragmentPlayerChipBinding)
        : RecyclerView.ViewHolder(playerChipBinding.root) {

        fun bind(playerScore: Player) {
            playerChipBinding.chip.text = playerScore.name

            val chip = playerChipBinding.chip

            chip.viewTreeObserver.addOnGlobalLayoutListener {
                val badgeDrawable = BadgeDrawable.create(playerChipBinding.root.context)
                badgeDrawable.number = playerScore.score
                badgeDrawable.verticalOffset = 25
                badgeDrawable.horizontalOffset = 15
                BadgeUtils.attachBadgeDrawable(badgeDrawable, chip, null)
                // chip.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

            chip.setOnClickListener {
                // TODO - this should switch to selected player's scoresheet
                Toast.makeText(chip.context, "Clicked on player ${playerScore.name}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = FragmentPlayerChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(this.currentList[position])
    }

    class PlayerDiff : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.name == newItem.name
        }
        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.name == newItem.name && oldItem.score == newItem.score

        }
    }
}
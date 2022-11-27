package com.nickpape.dicepokerbattleroyale.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerChipBinding
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel

data class PlayerScore(val id: String, val name: String, val score: Int, val selected: Boolean, var isPlayerTurn: Boolean)

class PlayerChipAdapter(val viewModel: MainViewModel) : ListAdapter<PlayerScore, PlayerChipAdapter.VH>(PlayerDiff()) {

    // ViewHolder pattern
    inner class VH(val playerChipBinding: FragmentPlayerChipBinding)
        : RecyclerView.ViewHolder(playerChipBinding.root) {

        var badge: BadgeDrawable? = null

        fun bind(playerScore: PlayerScore) {
            playerChipBinding.chip.text = playerScore.name

            playerChipBinding.chip.setBackgroundColor(
                if (playerScore.selected) {
                    Color.LTGRAY
                } else {
                    Color.TRANSPARENT
                }
            )

            val chip = playerChipBinding.chip

            if (badge != null) {
                Log.d(javaClass.simpleName, "Removing badge")
                BadgeUtils.detachBadgeDrawable(badge!!, chip)
            }

            badge = BadgeDrawable.create(playerChipBinding.root.context)
            badge!!.verticalOffset = 25
            badge!!.horizontalOffset = 15
            badge!!.number = playerScore.score

            chip.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    BadgeUtils.attachBadgeDrawable(badge!!, chip, null)
                    chip.viewTreeObserver.removeOnGlobalLayoutListener(this);

                    Log.d(javaClass.simpleName, "Adding badge")
                }
            })

            chip.setOnClickListener {
                viewModel.selectPlayer(playerScore.id)
                Toast.makeText(chip.context, "Switched to ${playerScore.name}'s Score Sheet", Toast.LENGTH_SHORT).show()
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

    class PlayerDiff : DiffUtil.ItemCallback<PlayerScore>() {
        override fun areItemsTheSame(oldItem: PlayerScore, newItem: PlayerScore): Boolean {
            return oldItem.name == newItem.name && oldItem.score == newItem.score
        }
        override fun areContentsTheSame(oldItem: PlayerScore, newItem: PlayerScore): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.score == newItem.score
                    && oldItem.selected == newItem.selected

        }
    }
}
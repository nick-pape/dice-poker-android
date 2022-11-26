package com.nickpape.dicepokerbattleroyale.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerRankRowBinding

class PlayerRankAdapter(): ListAdapter<PlayerScore, PlayerRankAdapter.VH>(PlayerChipAdapter.PlayerDiff()) {
    inner class VH(private val playerRankRowBinding: FragmentPlayerRankRowBinding)
        : RecyclerView.ViewHolder(playerRankRowBinding.root) {

            fun bind(playerScore: PlayerScore, position: Int) {
                playerRankRowBinding.playerName.text = playerScore.name
                playerRankRowBinding.rank.text = rankToString(position)
                playerRankRowBinding.score.text = playerScore.score.toString()

                if (position == 1) {
                    playerRankRowBinding.rank.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24F)
                } else {
                    playerRankRowBinding.rank.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12F)
                }
            }

            private fun rankToString(position: Int): String {
                return when (position % 10) {
                    1 -> "${position}st"
                    2 -> "${position}nd"
                    3 -> "${position}rd"
                    else -> "${position}th"
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = FragmentPlayerRankRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(this.currentList[position], position + 1)
    }
}
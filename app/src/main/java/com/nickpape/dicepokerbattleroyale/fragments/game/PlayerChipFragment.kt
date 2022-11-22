package com.nickpape.dicepokerbattleroyale.fragments.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.nickpape.dicepokerbattleroyale.databinding.FragmentPlayerChipBinding

class PlayerChipFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPlayerChipBinding.inflate(inflater, container, false)

        val chip = binding.chip

        chip.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val badgeDrawable = BadgeDrawable.create(binding.root.context)
                badgeDrawable.number = 128
                badgeDrawable.verticalOffset = 25
                badgeDrawable.horizontalOffset = 15
                BadgeUtils.attachBadgeDrawable(badgeDrawable, chip, null)
                chip.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        return binding.root
    }
}
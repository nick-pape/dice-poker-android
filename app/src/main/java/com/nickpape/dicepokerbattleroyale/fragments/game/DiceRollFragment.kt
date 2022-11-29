package com.nickpape.dicepokerbattleroyale.fragments.game

import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel
import com.nickpape.dicepokerbattleroyale.models.DiceRoll
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentDiceRollBinding

class DiceRollFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentDiceRollBinding? = null
    private val binding get() = _binding!!

    private fun createDiceClickListener(index: Int): ((View) -> Unit)? {
        return fun (_) {
            viewModel.toggleHoldDice(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDiceRollBinding.inflate(inflater, container, false)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            binding.diceOneImage.setSvgColor(R.color.primaryTextColor)
            binding.diceTwoImage.setSvgColor(R.color.primaryTextColor)
            binding.diceThreeImage.setSvgColor(R.color.primaryTextColor)
            binding.diceFourImage.setSvgColor(R.color.primaryTextColor)
            binding.diceFiveImage.setSvgColor(R.color.primaryTextColor)
        }

        binding.diceOneImage.setOnClickListener(createDiceClickListener(0))
        binding.diceTwoImage.setOnClickListener(createDiceClickListener(1))
        binding.diceThreeImage.setOnClickListener(createDiceClickListener(2))
        binding.diceFourImage.setOnClickListener(createDiceClickListener(3))
        binding.diceFiveImage.setOnClickListener(createDiceClickListener(4))

        binding.rollButton.setOnClickListener {
            viewModel.rollDice()
        }

        viewModel.isActiveUserTurn().observe(viewLifecycleOwner) {
            if (it) {
                binding.otherPlayerTurn.visibility = View.GONE
                binding.diceContainer.visibility = View.VISIBLE
            } else {
                val currentPlayerName = viewModel.currentPlayerInGame().value!!.display_name

                binding.otherPlayerTurn.text = "Waiting on $currentPlayerName's turn..."
                binding.otherPlayerTurn.visibility = View.VISIBLE
                binding.diceContainer.visibility = View.GONE
            }

        }

        viewModel.observePlayerDice().observe(viewLifecycleOwner) {
            setDiceImage(binding.diceOneImage, it[0])
            setDiceImage(binding.diceTwoImage, it[1])
            setDiceImage(binding.diceThreeImage, it[2])
            setDiceImage(binding.diceFourImage, it[3])
            setDiceImage(binding.diceFiveImage, it[4])
        }

        viewModel.observeDiceCount().observe(viewLifecycleOwner) {
            binding.rollCountText.text = "$it/3"
        }

        viewModel.canRollAgain.observe(viewLifecycleOwner) {
            binding.rollButton.isClickable = it
            binding.rollButton.visibility = if (it) { View.VISIBLE } else { View.INVISIBLE }
        }

        return binding.root
    }

    private fun setDiceImage(image: ImageView, dice: DiceRoll) {
        image.setImageResource(
            when (dice.value) {
                1 -> R.drawable.dice_one
                2 -> R.drawable.dice_two
                3 -> R.drawable.dice_three
                4 -> R.drawable.dice_four
                5 -> R.drawable.dice_five
                6 -> R.drawable.dice_six
                else -> R.drawable.dice_one
            }
        )

        if (dice.value == null) {
            image.setImageDrawable(null)
        }

        if (dice.isHeld) {
            image.setBackgroundResource(R.drawable.border)
        } else {
            image.background = null
        }
    }

    fun ImageView.setSvgColor(@ColorRes color: Int) =
        setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
}
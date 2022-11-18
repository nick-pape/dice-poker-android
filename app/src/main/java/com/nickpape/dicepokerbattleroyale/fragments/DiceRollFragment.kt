package com.nickpape.dicepokerbattleroyale.fragments

import android.content.res.Configuration
import android.graphics.Color
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
import com.nickpape.dicepokerbattleroyale.CreateGameViewModel
import com.nickpape.dicepokerbattleroyale.DiceRoll
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentDiceRollBinding
import com.nickpape.dicepokerbattleroyale.databinding.FragmentScoresheetBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiceRollFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiceRollFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: CreateGameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
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

        viewModel.observePlayerDice().observe(viewLifecycleOwner) {
            setDiceImage(binding.diceOneImage, it[0])
            setDiceImage(binding.diceTwoImage, it[1])
            setDiceImage(binding.diceThreeImage, it[2])
            setDiceImage(binding.diceFourImage, it[3])
            setDiceImage(binding.diceFiveImage, it[4])
        }

        viewModel.observePotentialScores().observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, it.toString())
        }

        viewModel.observeDiceCount().observe(viewLifecycleOwner) {
            binding.rollCountText.text = "$it/3"
        }

        viewModel.canRollAgain().observe(viewLifecycleOwner) {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiceRollFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiceRollFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
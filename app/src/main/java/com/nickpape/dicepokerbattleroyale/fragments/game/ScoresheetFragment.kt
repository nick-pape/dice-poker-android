package com.nickpape.dicepokerbattleroyale.fragments.game

import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nickpape.dicepokerbattleroyale.view_models.GameViewModel
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentDiceScoreBinding
import com.nickpape.dicepokerbattleroyale.databinding.FragmentScoresheetBinding

import kotlin.reflect.KMutableProperty

class ScoresheetFragment : Fragment() {

    private val viewModel: GameViewModel by activityViewModels()

    private var _binding: FragmentScoresheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScoresheetBinding.inflate(inflater, container, false)

        binding.scoreOne.diceScoreImage.setImageResource(R.drawable.dice_one)
        binding.scoreTwo.diceScoreImage.setImageResource(R.drawable.dice_two)
        binding.scoreThree.diceScoreImage.setImageResource(R.drawable.dice_three)
        binding.scoreFour.diceScoreImage.setImageResource(R.drawable.dice_four)
        binding.scoreFive.diceScoreImage.setImageResource(R.drawable.dice_five)
        binding.scoreSix.diceScoreImage.setImageResource(R.drawable.dice_six)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            binding.scoreOne.diceScoreImage.setSvgColor(R.color.primaryTextColor)
            binding.scoreTwo.diceScoreImage.setSvgColor(R.color.primaryTextColor)
            binding.scoreThree.diceScoreImage.setSvgColor(R.color.primaryTextColor)
            binding.scoreFour.diceScoreImage.setSvgColor(R.color.primaryTextColor)
            binding.scoreFive.diceScoreImage.setSvgColor(R.color.primaryTextColor)
            binding.scoreSix.diceScoreImage.setSvgColor(R.color.primaryTextColor)
        }

        binding.threeOfAKind.diceScoreImage.visibility = View.INVISIBLE
        binding.fourOfAKind.diceScoreImage.visibility = View.INVISIBLE
        binding.fullHouse.diceScoreImage.visibility = View.INVISIBLE
        binding.smallStraight.diceScoreImage.visibility = View.INVISIBLE
        binding.largeStraight.diceScoreImage.visibility = View.INVISIBLE
        binding.yahtzee.diceScoreImage.visibility = View.INVISIBLE
        binding.yahtzeeBonus.diceScoreImage.visibility = View.INVISIBLE
        binding.chance.diceScoreImage.visibility = View.INVISIBLE

        binding.threeOfAKind.diceScoreText.text = "3 of Kind"
        binding.fourOfAKind.diceScoreText.text = "4 of Kind"
        binding.fullHouse.diceScoreText.text = "Full House"
        binding.smallStraight.diceScoreText.text = "Small Straight"
        binding.largeStraight.diceScoreText.text = "Large Straight"
        binding.yahtzee.diceScoreText.text = "Yahtzee"
        binding.yahtzeeBonus.diceScoreText.text = "Bonus"
        binding.chance.diceScoreText.text = "Chance"

        viewModel.observePotentialScores().observe(viewLifecycleOwner) {
            val playerScoreSheet = viewModel.playerScoreSheet.value!!

            setPotentialScore(playerScoreSheet::ones, it.ones, binding.scoreOne)
            setPotentialScore(playerScoreSheet::twos, it.twos, binding.scoreTwo)
            setPotentialScore(playerScoreSheet::threes, it.threes, binding.scoreThree)
            setPotentialScore(playerScoreSheet::fours, it.fours, binding.scoreFour)
            setPotentialScore(playerScoreSheet::fives, it.fives, binding.scoreFive)
            setPotentialScore(playerScoreSheet::sixes, it.sixes, binding.scoreSix)

            setPotentialScore(playerScoreSheet::threeOfKind, it.threeOfKind, binding.threeOfAKind)
            setPotentialScore(playerScoreSheet::fourOfKind, it.fourOfKind, binding.fourOfAKind)
            setPotentialScore(playerScoreSheet::fullHouse, it.fullHouse, binding.fullHouse)
            setPotentialScore(playerScoreSheet::smallStraight, it.smallStraight, binding.smallStraight)
            setPotentialScore(playerScoreSheet::largeStraight, it.largeStraight, binding.largeStraight)
            setPotentialScore(playerScoreSheet::yahtzee, it.yahtzee, binding.yahtzee)
            setPotentialScore(playerScoreSheet::chance, it.chance, binding.chance)
        }


        return binding.root
    }

    fun setPotentialScore(
        field: KMutableProperty<Int?>,
        potentialScore: Int?,
        scoreBinding: FragmentDiceScoreBinding
    ) {
        if (field.getter.call() == null) {
            scoreBinding.scoreText.text = potentialScore.toString()

            scoreBinding.scoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18F)
            scoreBinding.scoreText.setTypeface(null, Typeface.BOLD_ITALIC)
            scoreBinding.scoreText.paintFlags = scoreBinding.scoreText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            scoreBinding.scoreText.setOnClickListener {
                field.setter.call(potentialScore)

                viewModel.playerScoreSheet.postValue(
                    viewModel.playerScoreSheet.value
                )
                viewModel.resetDice()
            }
        } else {
            scoreBinding.scoreText.setOnClickListener(null)

            scoreBinding.scoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25F)
            scoreBinding.scoreText.setTypeface(null, Typeface.NORMAL )
            scoreBinding.scoreText.paintFlags = scoreBinding.scoreText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            scoreBinding.scoreText.text = field.getter.call().toString()
        }
    }

    fun ImageView.setSvgColor(@ColorRes color: Int) =
        setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
}
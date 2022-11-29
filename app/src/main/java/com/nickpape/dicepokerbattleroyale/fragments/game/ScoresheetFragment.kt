package com.nickpape.dicepokerbattleroyale.fragments.game

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.jinatonic.confetti.CommonConfetti
import com.github.jinatonic.confetti.ConfettiSource
import com.nickpape.dicepokerbattleroyale.view_models.MainViewModel
import com.nickpape.dicepokerbattleroyale.R
import com.nickpape.dicepokerbattleroyale.databinding.FragmentDiceScoreBinding
import com.nickpape.dicepokerbattleroyale.databinding.FragmentScoresheetBinding
import com.nickpape.dicepokerbattleroyale.models.RawScoreSheet
import com.nickpape.dicepokerbattleroyale.models.ScoreSheet
import com.nickpape.dicepokerbattleroyale.models.ScoreableField

import kotlin.reflect.KMutableProperty

class ScoresheetFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentScoresheetBinding? = null
    private val binding get() = _binding!!

    data class DiceFieldBinding(
        val field: ScoreableField,
        val binding: FragmentDiceScoreBinding,
        val imageResource: Int? = null,
        val text: String? = null

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScoresheetBinding.inflate(inflater, container, false)

        val scoreFieldsList = listOf(
            DiceFieldBinding(ScoreableField.Ones, binding.scoreOne, imageResource = R.drawable.dice_one),
            DiceFieldBinding(ScoreableField.Twos, binding.scoreTwo, imageResource = R.drawable.dice_two),
            DiceFieldBinding(ScoreableField.Threes, binding.scoreThree, imageResource = R.drawable.dice_three),
            DiceFieldBinding(ScoreableField.Fours, binding.scoreFour, imageResource = R.drawable.dice_four),
            DiceFieldBinding(ScoreableField.Fives, binding.scoreFive, imageResource = R.drawable.dice_five),
            DiceFieldBinding(ScoreableField.Sixes, binding.scoreSix, imageResource = R.drawable.dice_six),

            DiceFieldBinding(ScoreableField.ThreeOfAKind, binding.threeOfAKind, text = "3 of Kind"),
            DiceFieldBinding(ScoreableField.FourOfAKind, binding.fourOfAKind, text = "4 of Kind"),
            DiceFieldBinding(ScoreableField.FullHouse, binding.fullHouse, text = "Full House"),
            DiceFieldBinding(ScoreableField.SmallStraight, binding.smallStraight, text = "Small Straight"),
            DiceFieldBinding(ScoreableField.LargeStraight, binding.largeStraight, text = "Large Straight"),
            DiceFieldBinding(ScoreableField.Yahtzee, binding.yahtzee, text = "Yahtzee"),
            DiceFieldBinding(ScoreableField.YahtzeeBonus, binding.yahtzeeBonus, text = "Bonus"),
            DiceFieldBinding(ScoreableField.Chance, binding.chance, text = "Chance")
        )

        binding.yahtzeeBonus.scoreText.setBackgroundResource(0)

        scoreFieldsList.forEach {
            if (it.imageResource != null) {
                it.binding.diceScoreImage.setImageResource(it.imageResource)

                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
                    it.binding.diceScoreImage.setSvgColor(R.color.primaryTextColor)
                }
            } else {
                it.binding.diceScoreImage.visibility = View.INVISIBLE
                it.binding.diceScoreText.text = it.text
            }
        }

        viewModel.playerScoreSheet().observe(viewLifecycleOwner) { scoresheet ->
            if (scoresheet == null) {
                return@observe
            }

            scoreFieldsList.forEach {
                setActualScore(scoresheet.getField(it.field), it)
            }
        }

        viewModel.observePotentialScores().observe(viewLifecycleOwner) { potentialScores ->
            if (potentialScores == null) {
                return@observe
            }

            val playerScoreSheet = viewModel.playerScoreSheet().value ?: return@observe
            val potentialScoreHelper = potentialScores.toScoreSheet()

            Log.d(javaClass.simpleName, potentialScoreHelper.toString())

            scoreFieldsList.forEach {
                setPotentialScore(it, potentialScoreHelper, playerScoreSheet)
            }
        }

        return binding.root
    }

    fun setActualScore(
        score: Int?,
        scoreFieldBinding: DiceFieldBinding
    ) {
        scoreFieldBinding.binding.scoreText.setOnClickListener(null)
        scoreFieldBinding.binding.scoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25F)
        scoreFieldBinding.binding.scoreText.setTypeface(null, Typeface.NORMAL )
        scoreFieldBinding.binding.scoreText.paintFlags = scoreFieldBinding.binding.scoreText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

        if (scoreFieldBinding.field == ScoreableField.YahtzeeBonus) {
            scoreFieldBinding.binding.scoreText.text = if (score == null) {
                ""
            } else {
                "+$score"
            }
        } else {
            scoreFieldBinding.binding.scoreText.text = score?.toString() ?: ""
        }
    }

    fun setPotentialScore(
        scoreFieldBinding: DiceFieldBinding,
        potentialScores: ScoreSheet,
        scoresheet: ScoreSheet
    ) {
        val currentScore = scoresheet.getField(scoreFieldBinding.field)
        val potentialScore = potentialScores.getFieldScore(scoreFieldBinding.field)

        if (currentScore == null && scoreFieldBinding.field !== ScoreableField.YahtzeeBonus) {
            scoreFieldBinding.binding.scoreText.text = potentialScore.toString()

            scoreFieldBinding.binding.scoreText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
            scoreFieldBinding.binding.scoreText.setTypeface(null, Typeface.BOLD_ITALIC)
            scoreFieldBinding.binding.scoreText.paintFlags = scoreFieldBinding.binding.scoreText.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            scoreFieldBinding.binding.scoreText.setOnClickListener {
                var isBonusYahtzeeEligible = scoresheet.getFieldScore(ScoreableField.Yahtzee) == 50
                        && potentialScores.getFieldScore(ScoreableField.Yahtzee) == 50

                // Handle the Yahtzee and Yahtzee bonus effects
                if (isBonusYahtzeeEligible || (scoreFieldBinding.field == ScoreableField.Yahtzee && potentialScore == 50)) {
                    val colors = IntArray(3)
                    colors[0] = Color.RED
                    colors[1] = Color.GREEN
                    colors[2] = Color.YELLOW
                    CommonConfetti.rainingConfetti(binding.root, colors).oneShot().animate()
                    Log.d(javaClass.simpleName, "CONFETTI!")

                    viewModel.playYahtzeeSound()
                }

                Log.d(javaClass.simpleName, "Dice sum: ${viewModel.diceSum().value ?: 0}")
                // Handle embarrassment of riches
                if (scoreFieldBinding.field == ScoreableField.FullHouse &&
                    (viewModel.diceSum().value ?: 0) > 25) {
                    Toast.makeText(context, "Embarrassment of Riches!", Toast.LENGTH_LONG).show()
                }

                // Handle Kobe and Jordan
                if (potentialScores.getFieldScore(scoreFieldBinding.field) == 24) {
                    Toast.makeText(context, "KOBE!", Toast.LENGTH_LONG).show()
                } else if (potentialScores.getFieldScore(scoreFieldBinding.field) == 23) {
                    Toast.makeText(context, "JORDAN!", Toast.LENGTH_LONG).show()
                }

                viewModel.updateScoresheet(scoreFieldBinding.field, potentialScore, isBonusYahtzeeEligible)
            }
        } else {
            setActualScore(currentScore, scoreFieldBinding)
        }
    }

    fun ImageView.setSvgColor(@ColorRes color: Int) =
        setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
}
package com.nickpape.dicepokerbattleroyale.fragments

import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private var _binding: FragmentDiceRollBinding? = null
    private val binding get() = _binding!!


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

        return binding.root
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
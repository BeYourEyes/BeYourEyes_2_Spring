package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentResultAlgBinding
import com.dna.beyoureyes.databinding.FragmentResultAlgCautionBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel
import com.google.android.material.chip.Chip

class ResultAllergyCautionFragment : Fragment() {
    private var _binding: FragmentResultAlgCautionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()
    private val userAllergyData = AppUser.info?.allergic?: mutableSetOf()
    private val detectedAllergyData = mutableSetOf<String>()

    private val maps = mutableMapOf<String, String>(Pair("메밀","buckewheat"), Pair("밀", "wheat"), Pair("우유", "milk"), Pair("대두", "bean"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultAlgCautionBinding.inflate(inflater, container, false)

        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.allergy?.forEach {
                Log.d("Allergy Caution", "${it}")
            }
            Log.d("Allergy Caution", "${detectedAllergyData} ${userAllergyData}")
        }

        binding.algCautionText1.setText(AppUser.info?.name + binding.algCautionText1.text)

        return binding.root
    }
}
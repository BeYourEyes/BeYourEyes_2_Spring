package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.databinding.FragmentResultAlgCautionBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel
import com.dna.extensions.addSubjectMarker

class ResultAllergyCautionFragment : Fragment() {
    private var _binding: FragmentResultAlgCautionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()
    private val userAllergyData = AppUser.info?.allergic?: mutableSetOf()
    private val detectedAllergyData = mutableSetOf<String>()
    val korToEng = mutableMapOf<String, String>(
        "메밀" to "buckwheat",
        "밀" to "wheat",
        "대두" to "bean",
        "땅콩" to "peanut",
        "호두" to "walnut",
        "잣" to "pinenut",
        "아황산" to "acid",
        "복숭아" to "peach",
        "토마토" to "tomato"
    )
    val engToKor = korToEng.entries.associate { (key, value) -> value to key }
    private var allergyCautionString = ""
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
            food.allergy?.forEach { allergy ->
                if (korToEng.containsKey(allergy)) {
                    detectedAllergyData.add(korToEng[allergy]!!)
                }
            }
            val commonAllergies = userAllergyData.intersect(detectedAllergyData.toSet())
            val commonAllergyInKorean = commonAllergies.mapNotNull { engToKor[it] }
            allergyCautionString = commonAllergyInKorean.joinToString(", ")
            Log.d("Allergy Caution", "${detectedAllergyData} ${userAllergyData} ${commonAllergies}")
            binding.algCautionText2.setText("이 식품에 ${allergyCautionString.addSubjectMarker()} 함유되어있어요")
        }

        binding.algCautionText1.setText(AppUser.info?.name + binding.algCautionText1.text)


        return binding.root
    }
}
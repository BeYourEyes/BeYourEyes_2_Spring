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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultAlgCautionBinding.inflate(inflater, container, false)

        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.allergy?.let { foodAlgs ->
                AppUser.info?.findMatchingAllergy(foodAlgs)?.let { commonAlgs ->
                    binding.algCautionText2.text = buildString {
                        append("이 식품에 ")
                        append(
                            commonAlgs.joinToString(", "){ it.displayName }
                                .addSubjectMarker() // 조사 붙이기
                        )
                        append(" 함유되어 있어요")
                    }
                }
            }
        }
        AppUser.info?.name?.let { userName ->
            binding.algCautionText1.text = userName + binding.algCautionText1.text
        }

        return binding.root
    }
}
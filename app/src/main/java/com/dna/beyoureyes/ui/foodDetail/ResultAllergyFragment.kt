package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentResultAlgBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel
import com.google.android.material.chip.Chip

class ResultAllergyFragment : Fragment() {

    private var _binding: FragmentResultAlgBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultAlgBinding.inflate(inflater, container, false)

        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.allergy?.forEach { alg ->
                // 칩 텍스트 및 사용자 조작 관련 설정
                val chip = Chip(context)
                chip.text =
                    if (alg.displayName.length == 1) " ${alg.displayName} " // 한 글자면 width 설정 불가
                    else alg.displayName
                chip.isChecked = true  // 클릭된 상태로 설정
                chip.isClickable = false  // 클릭 불가능
                chip.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO

                // 표시 관련 설정
                chip.layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.chip_width), // 85dp
                    resources.getDimensionPixelSize(R.dimen.chip_height) // 40dp
                ).also { params -> params.gravity = Gravity.CENTER }
                chip.setTextAppearanceResource(R.style.chipTextMyInfo)
                chip.setChipBackgroundColorResource(R.color.blue_50)
                chip.setChipStrokeColorResource(R.color.blue_300)

                binding.resultAlgChipGroup.addView(chip)
            }

            binding.algLayout.contentDescription = buildString {
                append(binding.algTitle.text) // 식품 알레르기 정보
                append(": ")
                append(
                    food.allergy?.joinToString(", ") { it.displayName }
                    ?.plus(" 함유")
                    ?: "없음"
                )
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
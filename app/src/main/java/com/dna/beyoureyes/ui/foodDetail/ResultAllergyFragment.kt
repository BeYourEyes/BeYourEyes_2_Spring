package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
            food.allergy?.forEach {
                val chip = Chip(requireContext())
                chip.text = it
                if (chip.text.length == 1) {
                    chip.text = " " + it + " " // 한글자인 경우에는 width 설정이 안됐음ㅜㅜ...
                }
                val params = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.chip_width), // 85dp
                    resources.getDimensionPixelSize(R.dimen.chip_height) // 40dp
                )
                params.gravity = Gravity.CENTER
                chip.layoutParams = params

                chip.setTextAppearanceResource(R.style.chipTextMyInfo)
                chip.isChecked = true  // 클릭된 상태로 설정
                chip.isClickable = false  // 클릭 불가능
                //chip.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_50))
                chip.setChipBackgroundColorResource(R.color.blue_50)
                chip.setChipStrokeColorResource(R.color.blue_300)
                binding.resultAlgChipGroup.addView(chip)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
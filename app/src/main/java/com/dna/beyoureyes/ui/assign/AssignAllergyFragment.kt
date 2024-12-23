package com.dna.beyoureyes.ui.assign

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignAllergyBinding
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.IconChip
import com.google.android.material.chip.Chip

class AssignAllergyFragment : AssignFragment() {

    private lateinit var binding : FragmentAssignAllergyBinding
    private val viewModel : AssignViewModel by activityViewModels()
    private val allergenToChipIdMap = HashMap<Allergen, Int>() // 칩 ID와 Allergen Enum 값을 매핑하는 HashMap

    override val questionMsg: String get() = getString(R.string.assign_step5_question)
    override val announceForAccessibilityMsg: String get() = questionMsg

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignAllergyBinding.inflate(inflater, container, false)
        viewModel.allergenSet?: run { binding.chipNone.isChecked = true }

        Allergen.entries.forEach { alg ->
            // 칩 스타일 및 표시 관련 설정
            val chip = Chip(context)
            chip.text =if (alg.displayName.length == 1) " ${alg.displayName} " else alg.displayName
            chip.isCheckable = true
            if (viewModel.contains(alg)) chip.isChecked = true
            chip.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.checkable_chip_width),
                resources.getDimensionPixelSize(R.dimen.checkable_chip_height)
            ).also { it.gravity = Gravity.CENTER }

            // 칩 ID 할당
            chip.id = View.generateViewId()
            allergenToChipIdMap[alg] = chip.id // [알러지 정보] - [칩 ID] hashMap 등록

            // 알레르기 칩 check 상태 변경 리스너
            chip.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    viewModel.addToAllergenSet(alg)
                    binding.chipNone.isChecked = false // "없음" 칩 해제
                } else {
                    viewModel.removeFromAllergenSet(alg)
                }
            }

            // 그룹에 칩 추가
            binding.assignAllergyChipGroup.addView(chip)
        }

        // 없음 칩 check 상태 변경 리스너
        val chipGroup = binding.assignAllergyChipGroup
        binding.chipNone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.allergenSet?.let { selectedAllergens ->
                    for (id in selectedAllergens.mapNotNull{ allergenToChipIdMap[it] }){
                        chipGroup.findViewById<Chip>(id).isChecked = false // 선택된 알레르기 칩 해제
                    }
                    viewModel.clearAllergenSet() // 저장된 거 비우기
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            viewModel.allergenSet?.let { selectedAllergens ->
                for (id in selectedAllergens.mapNotNull{ allergenToChipIdMap[it] }){
                    binding.assignAllergyChipGroup
                        .findViewById<Chip>(id).isChecked = true
                }
            } ?: run {
                binding.chipNone.isChecked = true
            }
        }
    }

    // 유효성 검사
    override fun isInputValid(): Boolean {
        return true
    }

}
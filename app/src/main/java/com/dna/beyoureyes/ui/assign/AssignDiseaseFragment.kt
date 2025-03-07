package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignDiseaseBinding
import com.dna.beyoureyes.data.model.Disease
import com.dna.beyoureyes.ui.common.IconChip


class AssignDiseaseFragment : AssignFragment() {

    private lateinit var binding : FragmentAssignDiseaseBinding
    private val viewModel : AssignViewModel by activityViewModels()
    private val diseaseToChipIdMap = HashMap<Disease, Int>() // 칩 ID와 Disease Enum 값을 매핑하는 HashMap

    override val questionMsg: String get () = getString(R.string.assign_step4_question)
    override val announceForAccessibilityMsg: String get() = questionMsg

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignDiseaseBinding.inflate(inflater, container, false)

        val chipGroup = binding.assignDiseaseChipGroup
        Disease.entries.forEach { disease ->
            // 칩 스타일 및 표시 관련 설정
            val iconChip = IconChip(requireContext()) // 커스텀 아이콘 칩
            iconChip.setCheckableDiseaseChip(disease) // 수정 가능한 질환 칩 세팅

            // 칩 ID 할당
            iconChip.id = View.generateViewId()
            diseaseToChipIdMap[disease] = iconChip.id // [질병] - [칩 ID] hashMap 등록

            // 그룹에 칩 추가
            binding.assignDiseaseChipGroup.addView(iconChip)

            // 질환 칩 check 상태 변경 리스너
            iconChip.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    viewModel.addToDiseaseSet(disease)
                    binding.none.isChecked = false // "없음" 칩 해제
                } else {
                    viewModel.removeFromDiseaseSet(disease)
                }
            }
        }

        // 없음 칩 check 상태 변경 리스너
        binding.none.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.diseaseSet?.let { selectedDisease ->
                    for (id in selectedDisease.mapNotNull{ diseaseToChipIdMap[it] }){
                        chipGroup.findViewById<IconChip>(id).isChecked = false // 선택된 질환 칩 해제
                    }
                    viewModel.clearDiseaseSet() // 저장된 거 비우기
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            viewModel.diseaseSet?.let { selectedDisease ->
                Log.d("TEST", "step 4: ${selectedDisease.toString()}")
                for (id in selectedDisease.mapNotNull{ diseaseToChipIdMap[it] }){
                    binding.assignDiseaseChipGroup
                        .findViewById<IconChip>(id).isChecked = true
                }
            } ?: run {
                binding.none.isChecked = true
                Log.d("TEST", "step 5: no disease")
            }
        }
    }

    // 유효성 검사
    override fun isInputValid(): Boolean {
        return true
    }

}
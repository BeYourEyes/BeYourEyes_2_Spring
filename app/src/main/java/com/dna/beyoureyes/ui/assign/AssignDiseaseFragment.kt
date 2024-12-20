package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.databinding.FragmentAssignDiseaseBinding
import com.dna.beyoureyes.model.Disease
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import com.dna.beyoureyes.ui.IconChip


class AssignDiseaseFragment : Fragment() {
    private lateinit var binding : FragmentAssignDiseaseBinding
    private var diseaseSet : MutableSet<Disease> = mutableSetOf()
    private val diseaseToChipIdMap = HashMap<Disease, Int>() // 칩 ID와 Disease Enum 값을 매핑하는 HashMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignDiseaseBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        val chipGroup = binding.assignDiseaseChipGroup
        Log.d("TEST", "disease set: ${diseaseSet.toString()}")
        Disease.entries.forEach { disease ->
            // 칩 스타일 및 표시 관련 설정
            val iconChip = IconChip(requireContext()) // 커스텀 아이콘 칩
            iconChip.setCheckableDiseaseChip(disease) // 수정 가능한 질환 칩 세팅

            // 칩 ID 할당
            iconChip.id = View.generateViewId()
            diseaseToChipIdMap[disease] = iconChip.id // [질병] - [칩 ID] hashMap 등록
            Log.d("TEST", "Create Icon Chip id:${iconChip.id}")

            // 그룹에 칩 추가
            binding.assignDiseaseChipGroup.addView(iconChip)

            // 질환 칩 check 상태 변경 리스너
            iconChip.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    diseaseSet.add(disease)
                    binding.none.isChecked = false // "없음" 칩 해제
                } else {
                    diseaseSet.remove(disease)
                }
            }
        }

        // 없음 칩 check 상태 변경 리스너
        binding.none.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                for (id in diseaseSet.mapNotNull{ diseaseToChipIdMap[it] }){
                    chipGroup.findViewById<IconChip>(id).isChecked = false // 선택된 질환 칩 해제
                }
                diseaseSet.clear() // 저장된 거 비우기
            }
        }

        binding.nextBtn.setOnClickListener {
            listener?.onDiseaseInputRecieved(diseaseSet)
            listener?.onBtnClick(this, true)
        }
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                listener?.onBtnClick(this@AssignDiseaseFragment, false)
            }
        }
        return binding.root
    }
}
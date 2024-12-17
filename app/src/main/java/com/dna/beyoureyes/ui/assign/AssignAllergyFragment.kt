package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignAllergyBinding
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import com.google.android.material.chip.Chip

class AssignAllergyFragment : Fragment() {

    private lateinit var binding : FragmentAssignAllergyBinding
    private var allergyArray : ArrayList<String> = ArrayList()
    private var allergenSet : MutableSet<Allergen> = mutableSetOf()
    private val chipIdToAllergenMap = HashMap<Int, Allergen>() // 칩 ID와 Allergen Enum 값을 매핑하는 HashMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignAllergyBinding.inflate(inflater, container, false)

        Allergen.entries.forEach { alg ->
            // 칩 스타일 및 표시 관련 설정
            val chip = Chip(context)
            chip.text =if (alg.displayName.length == 1) " ${alg.displayName} " else alg.displayName
            chip.isCheckable = true
            chip.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.checkable_chip_width),
                resources.getDimensionPixelSize(R.dimen.checkable_chip_height)
            ).also { it.gravity = Gravity.CENTER }

            // 칩 ID 할당
            chip.id = View.generateViewId()
            chipIdToAllergenMap[chip.id] = alg // [칩 ID] - [알러지 정보] hashMap 등록

            // 알레르기 칩 check 상태 변경 리스너
            chip.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    allergenSet.add(alg)
                    binding.chipNone.isChecked = false // "없음" 칩 해제
                } else {
                    allergenSet.remove(alg)
                }
                Log.d("ASSIGN_ALLERGY",
                    "selected chips: ${allergenSet.joinToString(", ") { it.displayName }}"
                )
            }

            // 그룹에 칩 추가
            binding.assignAllergyChipGroup.addView(chip)
        }

        // 없음 칩 check 상태 변경 리스너
        val chipGroup = binding.assignAllergyChipGroup
        binding.chipNone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                for (id in chipGroup.checkedChipIds.intersect(chipIdToAllergenMap.keys)){
                    chipGroup.findViewById<Chip>(id).isChecked = false // 선택된 알레르기 칩 해제
                }
                allergenSet.clear() // 저장된 거 비우기
                Log.d("ASSIGN_ALLERGY",
                    "selected chips: ${allergenSet.joinToString(", ") { it.displayName }}"
                )
            }
        }

        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            listener?.onAllergyInputRecieved(allergenSet)
            listener?.onBtnClick(this, true)
        }
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                listener?.onBtnClick(this@AssignAllergyFragment, false)
            }
        }
        return binding.root
    }

}
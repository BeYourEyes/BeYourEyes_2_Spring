package com.dna.beyoureyes.ui.assign

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignGenderBinding

class AssignGenderFragment : AssignFragment() {
    private lateinit var binding : FragmentAssignGenderBinding
    override val questionMsg: String by lazy { getString(R.string.assign_step2_question) }
    override val announceForAccessibilityMsg: String by lazy { questionMsg }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignGenderBinding.inflate(inflater, container, false)

        binding.chipFemale.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.chipMale.isChecked = false
            }
        }
        binding.chipMale.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                binding.chipFemale.isChecked = false
            }
        }
        return binding.root
    }

    private fun getUserGender(): Int {
        if (binding.chipFemale.isChecked) return 0
        else if(binding.chipMale.isChecked) return 1
        else return -1
    }

    // 유효성 검사 & 입력 내용 getter
    override fun getValidInput(): Int? {
        val gender = getUserGender()
        if (gender != -1) {
            return gender
        } else {
            Toast.makeText(requireContext(), "성별을 입력해주세요.", Toast.LENGTH_LONG).show()
            return null
        }
    }
}
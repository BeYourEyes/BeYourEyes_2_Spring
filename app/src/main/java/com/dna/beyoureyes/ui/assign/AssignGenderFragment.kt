package com.dna.beyoureyes.ui.assign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignGenderBinding

class AssignGenderFragment : AssignFragment() {

    private lateinit var binding : FragmentAssignGenderBinding
    private val viewModel: AssignViewModel by activityViewModels()

    override val questionMsg: String
        get() = getString(R.string.assign_step2_question)
    override val announceForAccessibilityMsg: String
        get() = questionMsg

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignGenderBinding.inflate(inflater, container, false)

        binding.chipFemale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipMale.isChecked = false
            }
        }
        binding.chipMale.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                binding.chipFemale.isChecked = false
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.gender?.let { gender ->
            if (gender == 0) binding.chipFemale.isChecked = true
            else binding.chipMale.isCheckable = true
        }
    }

    private fun getUserGender(): Int {
        return if (binding.chipFemale.isChecked) 0
        else if(binding.chipMale.isChecked) 1
        else -1
    }

    // 유효성 검사
    override fun isInputValid(): Boolean {
        val gender = getUserGender()
        if (gender != -1) {
            viewModel.setGender(gender) // 유효하면 뷰모델에 값 저장
            return true
        } else {
            Toast.makeText(requireContext(), "성별을 입력해주세요.", Toast.LENGTH_LONG).show()
            return false
        }
    }
}
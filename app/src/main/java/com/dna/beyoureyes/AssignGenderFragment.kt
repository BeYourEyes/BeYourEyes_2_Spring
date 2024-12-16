package com.dna.beyoureyes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dna.beyoureyes.databinding.FragmentAssignGenderBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener

class AssignGenderFragment : Fragment() {
    private lateinit var binding : FragmentAssignGenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignGenderBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            val gender = getUserGender()
            if (gender != -1) {
                listener?.onGenderInputRecieved(gender)
                listener?.onBtnClick(this, true)
            }
            else {
                Toast.makeText(requireContext(), "성별을 입력해주세요.", Toast.LENGTH_LONG).show()
            }
        }

        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                listener?.onBtnClick(this@AssignGenderFragment, false)
            }
        }

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
}
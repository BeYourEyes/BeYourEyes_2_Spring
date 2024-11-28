package com.dna.beyoureyes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dna.beyoureyes.databinding.FragmentAssignAllergyBinding
import com.dna.beyoureyes.databinding.FragmentAssignBirthBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import com.google.android.material.chip.Chip

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AssignAllergyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssignAllergyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentAssignAllergyBinding
    private var allergyArray : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignAllergyBinding.inflate(inflater, container, false)
        binding.assignAllergyChipGroup.setOnCheckedStateChangeListener{ group, checkedIds ->
            // "없음" 클릭 시 다른 칩 해제
            if (checkedIds.contains(R.id.chip_none)) {
                for (chipId in group.checkedChipIds) {
                    if (chipId != R.id.chip_none) {
                        group.findViewById<Chip>(chipId).isChecked = false
                    }
                }
            }
            // 다른 칩 클릭 시 "없음" 해제
            else {
                group.findViewById<Chip>(R.id.chip_none)?.isChecked = false
            }

            // 선택된 칩 리스트 업데이트
            allergyArray.clear()
            for (chipId in group.checkedChipIds) {
                val chip: Chip = group.findViewById(chipId)
                allergyArray.add(resources.getResourceEntryName(chip.id).substring(5))
            }
        }


        // Inflate the layout for this fragment
        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            listener?.onAllergyInputRecieved(allergyArray)
            listener?.onBtnClick(this, true)
        }
        binding.toolbar.backButtonClickListener = object : CustomToolbar.BackButtonClickListener {
            override fun onBackButtonClicked() {
                listener?.onBtnClick(this@AssignAllergyFragment, false)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AssignAllergyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssignAllergyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
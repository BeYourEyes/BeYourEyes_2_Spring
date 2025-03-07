package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.FoodActivity
import com.dna.beyoureyes.databinding.FragmentFoodCautionBinding
import com.dna.beyoureyes.ui.common.CustomToolbar


class FoodCautionFragment : Fragment() {
    private var _binding: FragmentFoodCautionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentFoodCautionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                requireActivity().finish()
            }
        }
        binding.toolbar.setSkipButtonButtonVisibility(false)

        // 확인하기 버튼 기능 연결
        binding.nextBtn.setOnClickListener {
            (requireActivity() as FoodActivity).startCamera()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
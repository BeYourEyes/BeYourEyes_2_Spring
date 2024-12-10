package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.FoodActivity
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentCameraFailBinding
import com.dna.beyoureyes.ui.CustomToolbar

class CameraFailFragment : Fragment() {
    private var _binding: FragmentCameraFailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentCameraFailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (viewModel.isCaptureSuccessful()) {
            // 촬영 성공하였으나 OCR 실패
            binding.cautionMainText.text = getString(R.string.ocr_fail_main_text)
            binding.cautionSubText.text = getString(R.string.ocr_fail_sub_text)
        }else {
            // 촬영 실패 (초점 문제)
            binding.cautionMainText.text = getString(R.string.camera_fail_main_text)
            binding.cautionSubText.text = getString(R.string.camera_fail_sub_text)

        }

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        // 확인하기 버튼 기능 연결
        binding.nextBtn.setOnClickListener {
            findNavController().popBackStack()
            (requireActivity() as FoodActivity).startCamera()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.databinding.FragmentResultFailBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel

class ResultFailFragment : Fragment() {

    private var _binding: FragmentResultFailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultFailBinding.inflate(inflater, container, false)
        if (viewModel.hasNoNutritionInfo()) { // 영양정보 없으면
            binding.failText.text = "영양성분 인식에 실패했어요."
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
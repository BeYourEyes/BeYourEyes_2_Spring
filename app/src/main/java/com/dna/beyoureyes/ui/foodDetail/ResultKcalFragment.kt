package com.dna.beyoureyes.ui.foodDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dna.beyoureyes.databinding.FragmentResultCalBinding
import com.dna.beyoureyes.ui.foodAnalysis.FoodViewModel

class ResultKcalFragment : Fragment() {

    private var _binding: FragmentResultCalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultCalBinding.inflate(inflater, container, false)

        viewModel.foodData.observe(viewLifecycleOwner) { food ->
            food.kcal?.let{ binding.kcalTextView.text = "${it}kcal" }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 외부에서 Bundle로 전달받은 칼로리 데이터
        val kcal : Int? = arguments?.getInt("kcal")
        kcal?.let { binding.kcalTextView.text = "${it}kcal" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentResultBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.foodDetail.ResultAllergyFragment
import com.dna.beyoureyes.ui.foodDetail.ResultKcalFragment
import com.dna.beyoureyes.ui.foodDetail.ResultNutriBarFragment


class FoodResultFragment: Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        // ChildFragmentManager 가져오기
        val fragmentManager = childFragmentManager


        // ViewModel에서 사진 Uri를 가져와 ImageView에 표시
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            binding.imageView.setImageURI(uri)
        }

        // 데이터 존재 여부에 따른 프래그먼트 조정 예시...
        // -> 표시할 프래그먼트 개수가 유동적이니... 아예 프래그먼트를 recyclerView로 관리할까?
        if (viewModel.isKcalValid()) { // 칼로리 정보 있으면 해당 프래그먼트 추가
            fragmentManager
                .beginTransaction()
                .replace(binding.TotalCalorieFragment.id, ResultKcalFragment())
                .commit()
        }

        if (viewModel.isNutritionDataValid()) { // 영양성분 정보 있으면 해당 프래그먼트 추가
            fragmentManager
                .beginTransaction()
                .replace(binding.NutriBarFragment.id, ResultNutriBarFragment())
                .commit()
        }
        if (viewModel.isAllergyDataValid()) { // 알레르기 정보 있으면 해당 프래그먼트 추가
            fragmentManager
                .beginTransaction()
                .replace(binding.AllergyFragment.id, ResultAllergyFragment())
                .commit()
        }

        // 먹기 버튼
        binding.resultButtonEat.setOnClickListener {
            findNavController().navigate(R.id.food_navi_eat)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
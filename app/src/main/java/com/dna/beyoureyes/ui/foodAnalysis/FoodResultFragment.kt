package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentResultBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.foodDetail.ResultAllergyCautionFragment
import com.dna.beyoureyes.ui.foodDetail.ResultAllergyFragment
import com.dna.beyoureyes.ui.foodDetail.ResultFailFragment
import com.dna.beyoureyes.ui.foodDetail.ResultKcalFragment
import com.dna.beyoureyes.ui.foodDetail.ResultNutriBarFragment
import com.dna.beyoureyes.ui.foodDetail.ResultNutriPieFragment
import com.dna.beyoureyes.util.TTSManager
import java.util.concurrent.atomic.AtomicBoolean


class FoodResultFragment: Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()
    private val resultFragments = mutableListOf<Fragment>()
    private val ttsManager by lazy { TTSManager.getInstance(requireContext()) } // 싱글톤

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
            resultFragments.add(ResultKcalFragment())
            fragmentManager
                .beginTransaction()
                .replace(binding.TotalCalorieFragment.id, ResultKcalFragment())
                .commit()
        }

        if (viewModel.isNutritionDataValid()) { // 영양성분 정보 있으면 해당 프래그먼트 추가
            resultFragments.add(ResultNutriBarFragment())
            fragmentManager
                .beginTransaction()
                .replace(binding.NutriBarFragment.id, ResultNutriBarFragment())
                .commit()
            fragmentManager
                .beginTransaction()
                .replace(binding.NutriPieFragment.id, ResultNutriPieFragment())
                .commit()
        }
        else {
            binding.NutriPieFragment.isVisible = false
            binding.NutriBarFragment.isVisible = false
            fragmentManager
                .beginTransaction()
                .replace(binding.NutriFailFragment.id, ResultFailFragment())
                .commit()
            Log.d("Result", "영양성분 인식 실패")
        }
        if (viewModel.isAllergyDataValid()) { // 알레르기 정보 있으면 해당 프래그먼트 추가
            resultFragments.add(ResultAllergyFragment())
            fragmentManager
                .beginTransaction()
                .replace(binding.AllergyFragment.id, ResultAllergyFragment())
                .commit()
            fragmentManager
                .beginTransaction()
                .replace(binding.AllergyCautionFragment.id, ResultAllergyCautionFragment())
                .commit()
        }
        else {
            binding.AllergyFragment.isVisible = false
            binding.AllergyCautionFragment.isVisible = false
            fragmentManager
                .beginTransaction()
                .replace(binding.NutriFailFragment.id, ResultFailFragment())
                .commit()
            Log.d("Result", "알레르기 인식 실패")
        }

        // TTS 재생 상태 변경 리스너 설정 - UI 업뎃을 위한 콜백 정의
        binding.resultButtonVoice // 버튼 초기화
        ttsManager.setTTSStateListener(object: TTSManager.TTSStateListener {
            override fun onTTSStarted() {
                binding.resultButtonVoice.text = "재생 중 / ■"
            }
            override fun onTTSDone() {
                binding.resultButtonVoice.text = "다시 듣기 / ▶"
            }
        })


        // 읽어 주기 버튼 (이 뷰에선 데이터 갱신될 일 없으니 observe가 아닌 초기에 한 번만 값을 읽기
        val ttsToastShowed = AtomicBoolean(false) // 일회용(최초 한번만 보여주기)
        viewModel.foodData.value?.let { food ->
            binding.resultButtonVoice.setOnClickListener {
                if (ttsManager.isSpeaking()) {
                    ttsManager.stop()
                } else {
                    ttsManager.speakNutritionalInfo(food)
                    if (ttsToastShowed.compareAndSet(false, true)) {
                        Toast.makeText(
                            requireContext(),
                            "재생을 멈추려면 버튼을 다시 눌러주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // 먹기 버튼
        binding.resultButtonEat.setOnClickListener {
            ttsManager.stop()
            findNavController().navigate(R.id.food_navi_eat)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown() // 엔진 종료
    }

}
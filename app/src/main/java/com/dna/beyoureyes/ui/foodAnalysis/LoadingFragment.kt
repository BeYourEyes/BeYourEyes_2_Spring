package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentLoadingBinding
import com.dna.beyoureyes.ui.common.CustomToolbar
import com.dna.beyoureyes.util.FoodTextRecognizer
import kotlinx.coroutines.launch

class LoadingFragment : Fragment() {
    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()

    lateinit var recognizer : FoodTextRecognizer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        recognizer = FoodTextRecognizer(requireContext())

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recognizer.progress.collect { progress -> // recognizer의 분석 진행도 값 가져오기
                    binding.progressBar.progress = progress
                    binding.progressPercentage.text = "$progress%"
                }
            }
        }

        viewModel.getOcrInputUri()?.let{ uri ->
            lifecycleScope.launch {
                recognizer.detectTextFromUri(uri) // 식품 분석 시작
                    ?.let { // 성공
                        viewModel.setFoodData(it)
                        // Log.d("LOADING", "Detection Succeed")
                        findNavController().navigate( // 현재 프래그먼트 종료하고 결과 프래그먼트 실행
                            R.id.food_navi_result, null,
                            navOptions {
                                popUpTo(findNavController().graph.startDestinationId)
                            })
                    }
                    ?:run { // 실패
                        // Log.d("LOADING", "Detection Failed")
                        findNavController().navigate( // 현재 프래그먼트 종료하고 오류 프래그먼트 실행
                            R.id.food_navi_fail, null,
                            navOptions {
                                popUpTo(findNavController().graph.startDestinationId)
                            })
                    }
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
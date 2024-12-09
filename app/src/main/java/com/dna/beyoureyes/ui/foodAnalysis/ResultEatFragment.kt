package com.dna.beyoureyes.ui.foodAnalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.databinding.FragmentResultEatBinding
import com.dna.beyoureyes.ui.CustomToolbar

class ResultEatFragment : Fragment() {
    private var _binding: FragmentResultEatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 레이아웃 바인딩
        _binding = FragmentResultEatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 뒤로 가기 버튼 기능 연결
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                findNavController().popBackStack()
            }
        }

        // ViewModel에서 사진 Uri를 가져와 ImageView에 표시
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            binding.ImageCaptured.setImageURI(uri)
        }

        binding.resultButtonEat.setOnClickListener {
            // 식품 기록 데이터 전송 후 종료해야 함. 전송 로직 작성 필요.
            // viewModel에 Food 형식의 데이터로 필요한 데이터 저장.
            // viewModel에 적절한 get 함수를 짜는 등...으로 칼로리, 영양정보 데이터 가져오기
            // 참고로 Food 객체에 양 조절 메소드 있음. (Food 변수).scaleQuantityByFactor(0.5)처럼 사용
            requireActivity().finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
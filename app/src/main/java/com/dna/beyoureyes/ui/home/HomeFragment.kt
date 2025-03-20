package com.dna.beyoureyes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // 뷰 바인딩
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 리사이클러 뷰 (오늘의 상세 영양소 섭취량) 바인딩
        binding.todayNutriRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 사용자 이름 표시
        binding.todayKcalLabel.text = buildString {
            append(AppUser.info?.name ?: "")
            append(getString(R.string.home_today_kcal_label))
        }

        // LiveData 옵저빙 및 UI 업데이트
        // 사용자 섭취량 표시
        homeViewModel.totalKcal.observe(viewLifecycleOwner) { kcal ->
            binding.kcalToday.text = "$kcal"
            updateContentDescriptions()  // 값 변경 시 contentDescription 업데이트
        }
        // 사용자 권장 섭취량 표시
        homeViewModel.energyRequirement.observe(viewLifecycleOwner) { requirement ->
            binding.kcalGoal.text = "$requirement"
            updateContentDescriptions()
        }
        // 사용자 섭취량 평가(실제 섭취량과 권장섭취량의 차)
        homeViewModel.topMsgKcal.observe(viewLifecycleOwner) { topMsgKcal ->
            binding.topMsgKcal.text = topMsgKcal
            updateContentDescriptions()
        }
        // 사용자 섭취량 평가(권장섭취 대비 실제 섭취량이 적은지/많은지)
        homeViewModel.topMsg2.observe(viewLifecycleOwner) { topMsg2 ->
            binding.topMsg2.text = topMsg2
            updateContentDescriptions()
        }
        // 사용자 섭취량 평가(조언)
        homeViewModel.topMsgSmall.observe(viewLifecycleOwner) { topMsgSmall ->
            binding.topMsgSmall.text = topMsgSmall
            updateContentDescriptions()
        }
        // 사용자 섭취량 캐릭터 이미지
        homeViewModel.topCharacterResId.observe(viewLifecycleOwner) { resId ->
            binding.topCharacter.setImageResource(resId)
        }
        // 리사이클러뷰 어댑터 데이터 설정
        homeViewModel.nutriIntakeItems.observe(viewLifecycleOwner) { nutritionList ->
            binding.todayNutriRecyclerView.adapter = IntakeAdapter(nutritionList.toMutableList())
        }

        return root
    }

    // contentDescription을 업데이트하는 함수
    private fun updateContentDescriptions() {
        // 상단 메시지 레이아웃(식사량 평가)의 contentDescription 업데이트
        binding.topMsgLayout.contentDescription = buildString {
            append("오늘 당신의 식사량은 필요 에너지량보다 ")
            append("${binding.topMsgKcal.text} kcal ")
            append("${binding.topMsg2.text}. ") // 적습니다 or 많습니다
            append(binding.topMsgSmall.text) // 조언
        }
        // 오늘 칼로리 레이아웃의 contentDescription 업데이트
        binding.todayKcalLayout.contentDescription = buildString {
            val userName = AppUser.info?.name ?: "사용자"
            append("$userName 님은, 오늘 필요 에너지량 ")
            append("${binding.kcalGoal.text} kcal 중, ")
            append("${binding.kcalToday.text} kcal를 섭취했습니다.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.dna.beyoureyes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.MainActivity
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentHomeBinding
import com.dna.beyoureyes.model.Carbs
import com.dna.beyoureyes.model.Cholesterol
import com.dna.beyoureyes.model.Fat
import com.dna.beyoureyes.model.Natrium
import com.dna.beyoureyes.model.Nutrition
import com.dna.beyoureyes.model.Protein
import com.dna.beyoureyes.model.SaturatedFat
import com.dna.beyoureyes.model.Sugar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IntakeAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // 뷰 바인딩
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 리사이클러 뷰 (오늘의 상세 영양소 섭취량) 바인딩
        recyclerView = binding.todayNutriRecyclerView
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager // 리사이클러 뷰 레이아웃 매니저 설정

        // 영양소별 섭취량 합산
        val activity = requireActivity() as MainActivity
        val nutritionTotals = activity.foodHistoryItems.flatMap { it.nutritions }
            .groupingBy { it::class }
            .fold(0) { acc, nutrition -> acc + nutrition.milligram }

        // 사용자의 칼로리 섭취량 합산
        val totalKcal = activity.foodHistoryItems.sumOf { it.kcal ?: 0 }

        // 사용자의 목표 칼로리 섭취량
        val energyRequirement = AppUser.info?.getDailyEnergyRequirement() ?: 2000

        // 칼로리 섭취 정보 설정
        binding.todayKcalLabel.text = buildString {
            append(AppUser.info?.name ?: "")
            append(getString(R.string.home_today_kcal_label))
        }
        binding.kcalGoal.text = "$energyRequirement"
        binding.kcalToday.text = "$totalKcal"

        // 사용자의 칼로리 섭취량과 필요 에너지량 비교 및 평가
        if (totalKcal < energyRequirement) { // 필요 에너지량보다 섭취량 적을 시
            binding.topMsgKcal.text = "${energyRequirement - totalKcal}" // 두 값 차 설정
            binding.topMsg2.text = getText(R.string.home_overview_msg2_when_less) // 적습니다
            binding.topMsgSmall.text = getText(R.string.home_advice_when_less) // 조언 텍스트
            binding.topCharacter.setImageResource(R.drawable.home_sad) // 캐릭터 반응
        } else {
            val diff = totalKcal - energyRequirement
            binding.topMsgKcal.text = "$diff" // 두 값 차 설정
            binding.topMsg2.text = getText(R.string.home_overview_msg2_when_more) // 많습니다
            if (diff <= 200) { // 권장량 + 200kcal까지는 적정 범위로 임의 설정.
                binding.topMsgSmall.text = getText(R.string.home_advice_when_good)
                binding.topCharacter.setImageResource(R.drawable.home_good)
            }else{ // 과다 섭취 경고 범위
                binding.topMsgSmall.text = getText(R.string.home_advice_when_more)
                binding.topCharacter.setImageResource(R.drawable.home_sad)
            }
        }

        // 영양소별 섭취량 리스트(오늘의 상세 영양소 리사이클러 뷰 어댑터에 넣기 위함)
        val nutriIntakeItems = mutableListOf<Nutrition>()

        // 리사이클러 뷰용 데이터 형식으로 변경.
        nutriIntakeItems.add(Carbs(nutritionTotals[Carbs::class] ?: 0))
        nutriIntakeItems.add(Sugar(nutritionTotals[Sugar::class] ?: 0))
        nutriIntakeItems.add(Protein(nutritionTotals[Protein::class] ?: 0))
        nutriIntakeItems.add(Natrium(nutritionTotals[Natrium::class] ?: 0))
        nutriIntakeItems.add(Cholesterol(nutritionTotals[Cholesterol::class] ?: 0))
        nutriIntakeItems.add(Fat(nutritionTotals[Fat::class] ?: 0))
        nutriIntakeItems.add(SaturatedFat(nutritionTotals[SaturatedFat::class] ?: 0))

        adapter = IntakeAdapter(nutriIntakeItems)
        recyclerView.adapter = adapter // 리사이클러 뷰 어댑터 설정


        // 스크린 리더용 contentDescription 설정
        binding.topMsgLayout.contentDescription = buildString { // 상단 식사량 평가
            append("오늘 당신의 식사량은 필요 에너지량보다")
            append("${binding.topMsgKcal.text} 칼로리 ")
            append("${binding.topMsg2.text}. ") // 적습니다 or 많습니다
            append(binding.topMsgSmall.text) // 조언
        }

        binding.todayKcalLayout.contentDescription = buildString { // 칼로리 총 섭취량
            /*
            append("${binding.todayKcalLabel.text}은 ") // ㅇㅇ님의 오늘 칼로리 섭취량은
            append("${binding.kcalToday.text} 칼로리 입니다. ")
            append("하루 필요 에너지량은 총 ${binding.kcalGoal.text} 칼로리예요.")
             */
            append("${AppUser.info?.name}님은, 오늘 필요 에너지량 $energyRequirement 칼로리 중 ")
            append("$totalKcal 칼로리를 섭취했습니다.")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
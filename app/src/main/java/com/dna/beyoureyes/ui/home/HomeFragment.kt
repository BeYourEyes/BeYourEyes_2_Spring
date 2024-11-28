package com.dna.beyoureyes.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.NutritionFacts
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

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

        // Firebase에서 사용자의 오늘 섭취량 데이터 가져오기
        val db = Firebase.firestore // Firebase 연결을 위한 설정값

        AndroidThreeTen.init(requireContext()) // api 레벨 최소 조건 때문에 java.time 대신 threeten으로 백포팅
        val current = LocalDateTime.now() // 오늘 날짜 가져오기
        val formatterForDB = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = current.format((formatterForDB)) // 오늘 날짜를 DB에 저장된 형식의 날짜 문자열로 변경

        var totalIntake = NutritionFacts() // 사용자의 총 섭취량 저장할 변수(구버전 형식)
        var energyIntake:Int = 0 // 사용자의 총 칼로리 섭취량
        val energyRequirement = AppUser.info?.getDailyEnergyRequirement() ?: 2000 // 사용자의 목표 칼로리 섭취량

        // DB에서 총 섭취량 가져오기
        db.collection("userIntakeNutrition")
            .whereEqualTo("userId", AppUser.id)
            .whereGreaterThanOrEqualTo("date", today) // 오늘 날짜 해당하는 것만
            .get()
            .addOnSuccessListener { result -> // DB 연결 성공
                if (!result.isEmpty) {
                    // 쿼리 결과 있는지 검사 = 오늘 섭취 기록 존재.
                    // 섭취량 합계 연산(일단 구버전 데이터 형식 사용)
                    for (document in result) {
                        Log.d("TODAYINTAKE", "${document.id} => ${document.data}")
                        val nutritionMap = document.data as? Map<String, Any?>
                        val calories = document.data.get("calories") as Any

                        var intake = NutritionFacts()

                        if (calories != null) {
                            intake.setEnergyValue(calories)
                        }
                        if (nutritionMap != null) {
                            intake.setNutritionValues(nutritionMap)
                        }
                        totalIntake += intake
                        Log.d("HOME", intake.natrium?.getMilliGram().toString())
                        Log.d("HOME", totalIntake.natrium?.getMilliGram().toString())
                    }
                    energyIntake = totalIntake.energy ?: 0 // 사용자의 칼로리 섭취량
                    Log.d("HOME", totalIntake.natrium?.getMilliGram().toString())
                } else {
                    Log.d("TODAYINTAKE", "user did not eat today")
                }
            }
            .addOnFailureListener { exception -> // DB 연결 실패 처리
                Log.w("HOME", "Error getting documents.", exception)
            }.addOnCompleteListener{ // DB 읽기를 마친 후 뷰 컴포넌트 세팅.

                // 칼로리 섭취 정보 설정
                binding.todayKcalLabel.text = buildString {
                    append(AppUser.info?.name ?: "")
                    append(getString(R.string.home_today_kcal_label))
                }
                binding.kcalGoal.text = buildString {
                    append(energyRequirement)
                    append("kcal")
                }
                binding.kcalToday.text = "$energyIntake"

                // 사용자의 칼로리 섭취량과 필요 에너지량 비교 및 평가
                if (energyIntake < energyRequirement) { // 필요 에너지량보다 섭취량 적을 시
                    binding.topMsgKcal.text = "${energyRequirement - energyIntake}" // 두 값 차 설정
                    binding.topMsg2.text = getText(R.string.home_overview_msg2_when_less) // 적습니다
                    binding.topMsgSmall.text = getText(R.string.home_advice_when_less) // 조언 텍스트
                    binding.topCharacter.setImageResource(R.drawable.home_sad) // 캐릭터 반응
                } else {
                    val diff = energyIntake - energyRequirement
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

                // Firebase에서 읽어온 값(구버전 코드, 데이터 형식) -> 리사이클러 뷰용 데이터 형식으로 변경.
                // 기록 없을 시 기본값을 0으로 대입.
                Log.d("HOME", totalIntake.natrium?.getMilliGram().toString())
                nutriIntakeItems.add(Carbs(totalIntake.carbs?.getMilliGram() ?: 0))
                nutriIntakeItems.add(Sugar(totalIntake.sugar?.getMilliGram() ?: 0))
                nutriIntakeItems.add(Protein(totalIntake.protein?.getMilliGram() ?: 0))
                nutriIntakeItems.add(Natrium(totalIntake.natrium?.getMilliGram() ?: 0))
                nutriIntakeItems.add(Cholesterol(totalIntake.chol?.getMilliGram() ?: 0))
                nutriIntakeItems.add(Fat(totalIntake.fat?.getMilliGram() ?: 0))
                nutriIntakeItems.add(SaturatedFat(totalIntake.satFat?.getMilliGram() ?: 0))

                adapter = IntakeAdapter(nutriIntakeItems)
                recyclerView.adapter = adapter // 리사이클러 뷰 어댑터 설정

            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
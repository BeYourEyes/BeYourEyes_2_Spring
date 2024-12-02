package com.dna.beyoureyes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dna.beyoureyes.databinding.ActivityMainBinding
import com.dna.beyoureyes.model.FoodHistory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    val foodHistoryItems = mutableListOf<FoodHistory>() // 음식 기록 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 바인딩
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 하단 네비게이션 뷰 초기화
        val navView: BottomNavigationView = binding.navView

        // 프래그먼트 뷰에 네비게이션 컨트롤러 연결
        // -> 네비게이션 파일에 설정한대로 처리할 수 있게
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // 프래그먼트 교체 시 하단 네비게이션 바를 숨겨야 할 때
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_past_detail) { // 과거 기록 뷰는 하단 바 숨김
                navView.visibility = View.GONE
            } else {
                // 그 외는 표시. 사라졌다가 등장시킬 시 애니메이션 효과 추가
                if (navView.visibility == View.GONE) {
                    val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
                    navView.startAnimation(slideIn)
                    navView.visibility = View.VISIBLE
                }
            }
        }

        // 오늘의 시작 시간 (자정)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time // 오늘 00:00:00

        // 오늘의 끝 시간 (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time // 오늘 23:59:59

        // Firestore 쿼리에 사용할 Timestamp 생성(오늘 날짜에 해당하는 timestamp 범위 검색)
        val startTimestamp = Timestamp(startOfDay)
        val endTimestamp = Timestamp(endOfDay)

        // Firebase에서 사용자의 오늘 섭취량 데이터 가져오기
        val db = Firebase.firestore // Firebase 연결을 위한 설정값

        // DB에서 총 섭취량 가져오기
        db.collection("userIntakeNutrition")
            .whereEqualTo("userId", AppUser.id)
            .whereEqualTo("userId", AppUser.id)
            .whereGreaterThanOrEqualTo("date", startTimestamp)
            .whereLessThanOrEqualTo("date", endTimestamp)       // 오늘 날짜 해당하는 것만
            .get()
            .addOnSuccessListener { result -> // DB 연결 성공
                if (!result.isEmpty) {
                    // 쿼리 결과 있는지 검사 = 오늘 섭취 기록 존재.
                    for (document in result) {
                        val history = FoodHistory(document) // 문서로부터 음식 기록 객체 생성
                        foodHistoryItems.add(history) // 리스트에 저장
                    }
                }
            }
            .addOnFailureListener { exception -> // DB 연결 실패 처리
                Log.w("HOME", "Error getting documents.", exception)
            }.addOnCompleteListener {
                // 데이터 로드를 마친 후 프래그먼트 화면 갱신
                navigateToHomeFragment()
            }
    }

    fun navigateToHomeFragment() {
        navController.navigate(R.id.navigation_home)
    }

    private fun Float.toDp(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    }

}
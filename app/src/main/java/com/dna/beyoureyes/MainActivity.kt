package com.dna.beyoureyes

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dna.beyoureyes.data.api.SpringApiResponseHandler
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.api.response.FoodHistoryResponse.Companion.toFoodHistory
import com.dna.beyoureyes.data.model.FoodHistory.Companion.sumNutritions
import com.dna.beyoureyes.databinding.ActivityMainBinding
import com.dna.beyoureyes.di.SpringClient
import com.dna.beyoureyes.ui.common.CustomDialog
import com.dna.beyoureyes.ui.home.HomeViewModel
import com.dna.beyoureyes.ui.myInfo.MyInfoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var myInfoViewModel: MyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myInfoViewModel = ViewModelProvider(this.applicationContext as App)[MyInfoViewModel::class.java]

        // 레이아웃 바인딩
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 하단 네비게이션 뷰 초기화
        val navView: BottomNavigationView = binding.navView

        // 프래그먼트 뷰에 네비게이션 컨트롤러 연결
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // 프래그먼트 교체 시 하단 네비게이션 바를 숨겨야 할 때
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_past_detail) { // 과거 기록 화면은 하단 바 숨김
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

        // Spring 서버에서 데이터 불러와 홈 화면에 반영
        lifecycleScope.launch {
            loadUserDataWithExceptionHandling()
            navController.navigate(R.id.navigation_home) // 프래그먼트 화면 갱신
        }
    }

    override fun onRestart() {
        super.onRestart()
        // 다시 시작할 때(FoodActivity 종료 후) HomeViewModel 데이터 업데이트 (총 섭취량 업데이트)
        myInfoViewModel.foodHistoryItems.value?.let{ foodHistories ->
            val kcal = foodHistories.sumOf { it.kcal }
            val nutritions = foodHistories.sumNutritions()
            homeViewModel.setUserIntakeData(kcal, nutritions)
        }
    }

    private suspend fun loadUserDataWithExceptionHandling() {
        val status = loadUserDataFromServer()
        when (status) {
            ApiStatus.SERVER_ERROR, ApiStatus.NETWORK_ERROR -> { // 서버 응답 에러 & 기타 오류(아마 네트워크 오류)
                CustomDialog(
                    msg = "서버와의 연결에 실패했습니다.\n네트워크 설정을 확인한 후\n다시 접속 해 주세요.",
                    buttonCallback = { finishAffinity() } // 현재 Task 내 모든 Activity 종료
                ).show(supportFragmentManager, "Dialog")
            } ApiStatus.UNKNOWN -> { // 알 수 없는 오류(null response body?)
                CustomDialog(
                    msg = "알 수 없는 오류가 발생했습니다.\n앱을 다시 시작해 주세요.",
                    buttonCallback = { finishAffinity() }
                ).show(supportFragmentManager, "Dialog")
            } else -> {} // 그 외 case 없음
        }
    }

    private suspend fun loadUserDataFromServer(): ApiStatus = suspendCancellableCoroutine { continuation ->
        lifecycleScope.launch {

            // 오늘 총 섭취량 합 읽기
            SpringApiResponseHandler {
                SpringClient.authSpringApi.getTodayIntakeSum()
            }.onSuccess { data, status ->
                when (status) {
                    ApiStatus.SUCCESS -> { // 응답 성공 시
                        // 응답 데이터를 HomeFragment용 뷰 모델에 저장
                        homeViewModel.setUserIntakeData(data!!.kcal, data.nutritionList)
                    } else -> { continuation.resume(status) } // 그 외 case 없음
                }
            }.onError { status ->
                continuation.resume(status)
            }.execute()

            // 오늘 섭취 기록 모두 불러오기
            SpringApiResponseHandler {
                SpringClient.authSpringApi.getTodayFoodHistories()
            }.onSuccess { data, status ->
                when (status) {
                    ApiStatus.SUCCESS -> { // 응답 성공 시
                        try {
                            val foodHistories = data!!.map { it.toFoodHistory() }.toMutableList() // 데이터 파싱
                            myInfoViewModel.setFoodHistory(foodHistories) // MyInfoFragment용 뷰 모델에 저장

                            continuation.resume(ApiStatus.SUCCESS)
                        } catch (e: IllegalArgumentException) {
                            // 응답을 FoodHistory 형식으로 변환 중 에러 발생
                            // 기록 저장 시간 -> LocalDateTime 변환 확인
                            Log.e("Today's FoodHistory Parsing Error", "$e")
                        }
                    } else -> {} // 그 외 case 없음
                }
                continuation.resume(status)
            }.onError { status ->
                continuation.resume(status)
            }.execute()
        }
    }
}
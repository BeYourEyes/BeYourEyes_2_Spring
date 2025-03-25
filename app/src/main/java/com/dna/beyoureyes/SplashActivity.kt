package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dna.beyoureyes.data.api.SpringApiResponseHandler
import com.dna.beyoureyes.databinding.ActivitySplashBinding
import com.dna.beyoureyes.data.api.request.DeviceIdRequest
import com.dna.beyoureyes.di.SpringClient
import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import com.dna.beyoureyes.data.api.model.ApiStatus
import com.dna.beyoureyes.data.repository.AuthRepositoryImpl
import com.dna.beyoureyes.ui.onboarding.OnboardingActivity
import com.dna.beyoureyes.data.local.TokenManager
import com.dna.beyoureyes.ui.common.CustomDialog
import com.google.firebase.installations.FirebaseInstallations
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume


class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_500)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    public override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val loginStatus = tryToLogin() // FID 얻고 Spring 서버에 로그인 시도
            when(loginStatus) {
                ApiStatus.SUCCESS -> { // 기존 사용자 (로그인 성공)
                    // 메인 화면으로 이동
                    delay(4000) // 4초 지연
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } ApiStatus.NO_DATA -> { // 신규 사용자 (FID와 일치하는 유저 없음)
                    // 온보딩 화면으로 이동
                    delay(4000) // 4초 지연
                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                    finish()
                } ApiStatus.SERVER_ERROR, ApiStatus.NETWORK_ERROR -> { // 서버 응답 에러 & 기타 오류(아마 네트워크 오류)
                    CustomDialog(
                        msg = "서버와의 연결에 실패했습니다.\n네트워크 설정을 확인한 후\n다시 접속 해 주세요.",
                        buttonCallback = { finish() }
                    ).show(supportFragmentManager, "Dialog")

                } ApiStatus.UNKNOWN -> { // 알 수 없는 오류(null response body?)
                    CustomDialog(
                        msg = "알 수 없는 오류가 발생했습니다.\n앱을 다시 시작해 주세요.",
                        buttonCallback = { finish() }
                    ).show(supportFragmentManager, "Dialog")
                }
            }
        }
    }

    private suspend fun tryToLogin(): ApiStatus = suspendCancellableCoroutine { continuation ->
        // 액세스 토큰 저장소 세팅
        val tokenManager = TokenManager(this)
        val authRepository = AuthRepositoryImpl(tokenManager)

        lifecycleScope.launch {
            val fid = getFirebaseInstallationId()  // FID(Firebase 설치 ID) 확인
            if (fid != null) { // FID 얻는 데 성공
                AppUser.setId(fid)
                // FID(Firebase 설치 ID)를 사용해 로그인 API 호출 시 필요한 DeviceID Request 객체 생성
                val deviceIdRequest = DeviceIdRequest(fid)
                // 로그인 API 호출
                SpringApiResponseHandler {
                    SpringClient.noAuthSpringApi.login(deviceIdRequest)
                }.onSuccess { data, status ->  // 응답 성공시 인증 토큰이 반환됨
                    when(status) {
                        ApiStatus.SUCCESS -> { // 기존 사용자
                            // 로그인 성공 (액세스 토큰 수신 성공)
                            val accessToken = data!!
                            authRepository.saveToken(accessToken)  // 토큰을 전용 Repository에 저장

                            // 액세스 토큰 인증을 위해 AuthInterceptor 설정
                            SpringClient.initAuthClient(AuthInterceptor(authRepository))

                            val loadUserInfoStatus = loadUserInfoFromServer() // 사용자 정보 조회
                            continuation.resume(loadUserInfoStatus) // 사용자 정보 조회 상태 반환

                        } ApiStatus.NO_DATA -> { // 신규 사용자
                            continuation.resume(ApiStatus.NO_DATA)
                        } else -> { } // 그 외 case 없음
                    }
                }.onError { status ->
                    continuation.resume(status)
                }.execute()
            } else { // FID를 얻는 데 실패
                continuation.resume(ApiStatus.NETWORK_ERROR)
            }
        }
    }

    private suspend fun loadUserInfoFromServer(): ApiStatus = suspendCancellableCoroutine { continuation ->
        lifecycleScope.launch {
            SpringApiResponseHandler {
                SpringClient.authSpringApi.getUserInfo()
            }.onSuccess { data, status ->
                when(status) {
                    ApiStatus.SUCCESS -> { // 응답 성공 시 유저 데이터 반환
                        try {
                            AppUser.setInfo(data!!) // 응답 데이터 파싱해 사용자 싱글톤 객체 업데이트
                            continuation.resume(ApiStatus.SUCCESS)
                        } catch (e: IllegalArgumentException){ // 파싱 실패 시
                            Log.e("SPRING_API_ERROR", "사용자 정보 변환 실패: ${e.message}")
                        }
                    } else -> { }
                }
                continuation.resume(ApiStatus.UNKNOWN)
            }.onError { status ->
                continuation.resume(status)
            }.execute()
        }
    }

    private suspend fun getFirebaseInstallationId(): String? {
        return try{
            val fid = FirebaseInstallations.getInstance().id.await()
            Log.d("FirebaseInstallationId", "FID: $fid")
            fid
        } catch (e: Exception) {
            Log.e("FirebaseInstallationId", "Error getting FID", e)
            null
        }
    }
}
package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.dna.beyoureyes.databinding.ActivitySplashBinding
import com.dna.beyoureyes.data.api.request.DeviceIdRequest
import com.dna.beyoureyes.di.SpringClient
import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import com.dna.beyoureyes.data.local.AppUser
import com.dna.beyoureyes.data.repository.AuthRepositoryImpl
import com.dna.beyoureyes.ui.onboarding.OnboardingActivity
import com.dna.beyoureyes.data.local.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var userId : String

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

        auth = Firebase.auth


    }

    public override fun onStart() {

        //Toast.makeText( this@SplashActivity, "Authentication onStart", Toast.LENGTH_SHORT).show()
        super.onStart()

        val tokenManager = TokenManager(this)
        val authRepository = AuthRepositoryImpl(tokenManager)

        CoroutineScope(Dispatchers.Main).launch {
            val fid = getFirebaseInstallationId()
            if (fid != null) {
                // FID를 사용하여 작업 수행
                Log.d("FirebaseInstallationId", "Received FID: $fid")
                val request = DeviceIdRequest(fid)
                try {
                    val response = SpringClient.loginApi.login(request)
                    if (response.isSuccessful) {
                        val registrationResponse = response.body()
                        Log.d("API_SUCCESS", "Registration Response: $registrationResponse")
                        // 응답 처리 (예: user_id, registration_date 사용)
                        if (registrationResponse != null) {
                            if (registrationResponse.status == "SUCCESS") {
                                val status = registrationResponse.status
                                val msg = registrationResponse.message
                                val access_token = registrationResponse.data
                                if (access_token != null){
                                    Log.d("API_SUCCESS", "status: $status, msg: $msg data: $access_token")
                                    authRepository.saveToken(access_token)
                                    val authInterceptor = AuthInterceptor(authRepository)
                                    Log.d("API_TEST", "1")
                                    val response = SpringClient.getUserInfoApi(authInterceptor).getUserInfo().body()
                                    Log.d("API_TEST", "2")

                                    response?.let{ userInfoResponse ->
                                        val status = userInfoResponse.status
                                        val msg = userInfoResponse.message
                                        val userData = userInfoResponse.data
                                        Log.d("API_USER_SUCCESS", "status: $status, msg: $msg data: $userData")
                                        AppUser.setInfo(userData)

                                    }?:run{
                                        Log.e("API_ERROR", "Server Can't Response Now")
                                    }
                                }
                                // 가입된 회원
                                delay(4000) // 3초 지연
                                AppUser.id = fid
                                withContext(Dispatchers.Main) {
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    finish()
                                }


                            } else if(registrationResponse.status == "ERROR") { // 미가입 시
                                Log.e("API_ERROR", "Error: ${registrationResponse.message}")

                                // 최초 접속 (데이터 없음) - 온보딩으로 이동
                                delay(4000) // 3초 지연
                                AppUser.id = fid
                                Log.d("SPLASH : ", " 최초 접속 + 데이터 없음 ${AppUser.id.toString()}")
                                withContext(Dispatchers.Main) {
                                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                                    finish()
                                }


                            }else {
                                Log.e("API_ERROR", "Error: ${registrationResponse.message}")
                            }
                        }
                    } else {
                        Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("API_EXCEPTION", "Exception: ${e.message}")
                }
            } else {
                // FID를 얻는 데 실패한 경우 처리
                Log.e("FirebaseInstallationId", "Failed to get FID")
            }
        }

        /*
        // Check if user is signed in (non-null) and update UI accordingly.
        lifecycleScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val hasData = FirebaseHelper.receiveUserData(currentUser) // suspend 함수로 변경
                Log.d("SPLASH", hasData.toString())
                if (hasData) {
                    //Toast.makeText(this@SplashActivity, "이미 가입한 유저", Toast.LENGTH_LONG).show()
                    userId = currentUser.uid
                    AppUser.id = userId
                    Log.d("SPLASH : ", AppUser.id.toString())
                    //FirebaseHelper.receiveUserData()
                    //Toast.makeText(this@SplashActivity, userId, Toast.LENGTH_LONG).show()
                    delay(4000) // 3초 지연
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    // 최초 접속 (데이터 없음) - 익명 로그인 후 온보딩으로 이동
                    signInAnonymously()
                    delay(4000) // 3초 지연
                    Log.d("SPLASH : ", " 최초 접속 + 데이터 없음 ${AppUser.id.toString()}")
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                        finish()
                    }
                }
            } else {
                // 최초 접속 (로그인 안됨) - 익명 로그인 후 온보딩으로 이동
                //Toast.makeText(this@SplashActivity, "가입안한 유저", Toast.LENGTH_LONG).show()
                signInAnonymously()
                delay(3000) // 3초 지연
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                    finish()
                }
            }
            updateUI(currentUser) // UI 업데이트
        }

         */
    }
    // [END on_start_check_user]

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

    private fun signInAnonymously() {
        // [START signin_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Toast.makeText(this@SplashActivity, "Authentication successed.", Toast.LENGTH_SHORT).show()
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SIGN", "signInAnonymously:success")
                    val user = auth.currentUser
                    // 과연 절대로 null이 아닐까?
                    userId = user!!.uid
                    AppUser.id = userId
                    Log.d("SPLASH : ", AppUser.id.toString()+"  INIT")
                    //Toast.makeText(this@SplashActivity, userId, Toast.LENGTH_LONG).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SIGN", "signInAnonymously:failure", task.exception)
                    //Toast.makeText( this@SplashActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END signin_anonymously]
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    companion object {
        private const val TAG = "AnonymousAuth"
    }
}
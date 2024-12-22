package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dna.beyoureyes.databinding.ActivitySplashBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var userId : String

    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_500)

        auth = Firebase.auth

        YoYo.with(Techniques.FadeIn)
            .duration(1500)
            .delay(0)
            .playOn(binding.splashText1)

        YoYo.with(Techniques.FadeIn)
            .duration(1500)
            .delay(0)
            .playOn(binding.splashText2)


    }

    public override fun onStart() {

        //Toast.makeText( this@SplashActivity, "Authentication onStart", Toast.LENGTH_SHORT).show()
        super.onStart()
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
    }
    // [END on_start_check_user]

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
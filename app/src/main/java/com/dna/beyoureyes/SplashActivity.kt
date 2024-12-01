package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dna.beyoureyes.databinding.ActivitySplashBinding
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.FirebaseHelper.Companion.receiveUserData
import com.dna.beyoureyes.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var userId : String

    private lateinit var binding : ActivitySplashBinding

    private var hasData = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // CoroutineScope 사용
        lifecycleScope.launch {
            hasData = receiveUserData()
            if (hasData) {
                // User data가 있을 때 처리
                Log.d("INFO", "User data exists")
            } else {
                // User data가 없을 때 처리
                Log.d("INFO", "No user data found")
            }
        }
        Log.d("INFO", "OnCreate endded")

    }

    public override fun onStart() {

        //Toast.makeText( this@SplashActivity, "Authentication onStart", Toast.LENGTH_SHORT).show()
        super.onStart()

        Log.d("INFO", "OnStart Started")

        YoYo.with(Techniques.Tada)
            .duration(1000)
            .delay(0)
            .pivotX(binding.appName.width/2.toFloat())
            .playOn(binding.appName)

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Log.d("INFO", "${currentUser} ${hasData}")
        // currentUser가 있고, receiveData를 진행 시 유저 데이터가 있던 경우
        if (currentUser != null && hasData) { // 앱 이용한 적 있는 유저
            //Toast.makeText(this@SplashActivity, "이미 가입한 유저", Toast.LENGTH_LONG).show()
            Log.d("FIREBASE", "${currentUser} ${hasData}")
            userId = currentUser.uid
            AppUser.id = userId
            //FirebaseHelper.receiveUserData()
            //Toast.makeText(this@SplashActivity, userId, Toast.LENGTH_LONG).show()
            Handler().postDelayed({ startActivity(Intent(this, MainActivity::class.java)); finish(); }, 3 * 1000)
        }
        else { // 최초 접속
            //Toast.makeText(this@SplashActivity, "가입안한 유저", Toast.LENGTH_LONG).show()
            signInAnonymously()
            Log.d("FIREBASE", "${currentUser} ${hasData}")
            Handler().postDelayed({ startActivity(Intent(this, OnboardingActivity::class.java)); finish(); }, 3 * 1000)
        }
        updateUI(currentUser)


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
package com.dna.beyoureyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView


class LoadingActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressPercentage: TextView
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // ProgressBar와 TextView를 초기화
        progressBar = findViewById(R.id.progress_bar)
        progressPercentage = findViewById(R.id.progress_percentage)

        // ProgressBar 업데이트 함수 실행
        updateProgress()

        // 5초 후에 메인 액티비티로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 로딩 액티비티 종료
        }, 10000) // 5초 후에 실행
    }

    // ProgressBar와 진행 상황을 업데이트하는 함수
    private fun updateProgress() {
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                runOnUiThread {
                    progressBar.progress = progressStatus
                    progressPercentage.text = "$progressStatus%" // 퍼센트 표시
                }
                Thread.sleep(80) // 50ms마다 진행률 업데이트
            }
        }.start()
    }
}

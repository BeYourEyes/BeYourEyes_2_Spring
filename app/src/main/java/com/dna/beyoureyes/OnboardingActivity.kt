package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val viewPager = findViewById<ViewPager>(R.id.register_viewpager)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        // 어댑터 설정
        val adapter = RegisterPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        dotsIndicator.setViewPager(viewPager)

        val startButton = findViewById<AppCompatButton>(R.id.button)
        startButton.setOnClickListener {
            //viewPager.currentItem = 0
            val intent = Intent(this, AssignActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
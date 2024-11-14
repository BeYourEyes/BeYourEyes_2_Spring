package com.dna.beyoureyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnbordingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onbording)

        val viewPager = findViewById<ViewPager>(R.id.register_viewpager)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)

        // 어댑터 설정
        val adapter = RegisterPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        dotsIndicator.setViewPager(viewPager)

        val startButton = findViewById<AppCompatButton>(R.id.button)
        startButton.setOnClickListener {
            viewPager.currentItem = 0
        }
    }
}
package com.dna.beyoureyes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.dna.beyoureyes.R
import com.dna.beyoureyes.RegisterPagerAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

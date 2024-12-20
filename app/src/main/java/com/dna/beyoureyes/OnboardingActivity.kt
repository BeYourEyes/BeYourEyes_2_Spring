package com.dna.beyoureyes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.dna.beyoureyes.databinding.ActivityOnboardingBinding
import com.dna.beyoureyes.ui.assign.AssignActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 바인딩
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager = binding.registerViewpager
        val dotsIndicator = binding.dotsIndicator

        // 어댑터 설정
        val adapter = RegisterPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // 스크롤 중일 때 동작
            }

            override fun onPageSelected(position: Int) {
                val pageNumber = position + 1
                val totalPages = adapter.count
                val pageInfo = "총 $totalPages 페이지 중 $pageNumber 페이지. "
                viewPager.announceForAccessibility(pageInfo) // 페이지 내용 읽기전 페이지 위치 알림

                /*
                val pageDescription = when(position) {
                    0 -> "나만을 위한 더 쉬운 식품 정보\n"+"당신의 안식은 시력이 좋지 않은 노인 및 식품 알레르기가 있는 사용자를 위한 어플입니다"
                    1 -> "카메라로 읽어주는 식품 정보 음성 인식 기능\n" + "카메라로 촬영한 식품 정보의 작은 글씨를 터치하면, 인공지능이 해당 정보를 음성으로 읽어줍니다."
                    2 -> "건강한 식사 습관 촉진과 식품 섭취 관련 문제 최소화\n" + "건강한 식사 습관을 촉진하고 식품 섭취와 관련된 문제를 최소화하는 것을 목표로 합니다."
                    else -> ""
                }
                 */

                val dotsContainer = dotsIndicator.getChildAt(0) as? ViewGroup
                dotsContainer?.let {
                    for (i in 0 until it.childCount) {
                        val dot = it.getChildAt(i)
                        dot.contentDescription = if (i == position) {
                            "현재 ${i + 1}페이지"
                        } else {
                            "${i + 1}페이지로 이동"
                        }
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
                // 스크롤 상태 변경 시 동작
            }
        })
        dotsIndicator.setViewPager(viewPager)

        val startButton = findViewById<AppCompatButton>(R.id.button)
        startButton.setOnClickListener {
            //viewPager.currentItem = 0
            val intent = Intent(this, AssignActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // 액티비티 실행 초기에 페이지 설명 제공
            binding.root.announceForAccessibility("앱의 기능을 소개하는 튜토리얼 페이지입니다." +
                    " 두 손가락을 우측으로 스와이프하면 페이지를 넘길 수 있습니다.")
        }
    }

    override fun onStart() {
        super.onStart()

        val viewPager = binding.registerViewpager
        val dotsIndicator = binding.dotsIndicator

        // viewPager가 레이아웃에 추가된 후 contentDescription을 설정
        viewPager.post {
            // dotsContainer 초기 설정
            val dotsContainer = dotsIndicator.getChildAt(0) as? ViewGroup
            dotsContainer?.let {
                for (i in 0 until it.childCount) {
                    val dot = it.getChildAt(i)
                    dot.contentDescription = if (i == 0) {
                        "현재 ${i + 1}페이지"
                    } else {
                        "${i + 1}페이지로 이동"
                    }
                }
            }
        }
    }

}
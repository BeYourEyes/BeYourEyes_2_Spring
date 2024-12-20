package com.dna.beyoureyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.dna.beyoureyes.ui.assign.AssignActivity
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

        // 초기 페이지 contentDescription 설정
        val initialPageDescription = "나만을 위한 더 쉬운 식품 정보\n당신의 안식은 시력이 좋지 않은 노인 및 식품 알레르기가 있는 사용자를 위한 어플입니다"
        viewPager.contentDescription = "튜토리얼: $initialPageDescription"
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


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // 스크롤 중일 때 동작
            }

            override fun onPageSelected(position: Int) {
                val pageDescription = when(position) {
                    0 -> "나만을 위한 더 쉬운 식품 정보\n"+"당신의 안식은 시력이 좋지 않은 노인 및 식품 알레르기가 있는 사용자를 위한 어플입니다"
                    1 -> "카메라로 읽어주는 식품 정보 음성 인식 기능\n" + "카메라로 촬영한 식품 정보의 작은 글씨를 터치하면, 인공지능이 해당 정보를 음성으로 읽어줍니다."
                    2 -> "건강한 식사 습관 촉진과 식품 섭취 관련 문제 최소화\n" + "건강한 식사 습관을 촉진하고 식품 섭취와 관련된 문제를 최소화하는 것을 목표로 합니다."
                    else -> ""
                }
                viewPager.contentDescription = "$pageDescription"
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

    override fun onStart() {
        super.onStart()

        val viewPager = findViewById<ViewPager>(R.id.register_viewpager)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        // viewPager가 레이아웃에 추가된 후 contentDescription을 설정
        viewPager.post {
            // 초기 페이지 contentDescription 설정
            val initialPageDescription = "나만을 위한 더 쉬운 식품 정보\n당신의 안식은 시력이 좋지 않은 노인 및 식품 알레르기가 있는 사용자를 위한 어플입니다"
            viewPager.contentDescription = "튜토리얼: $initialPageDescription"

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

    private fun updateDotsContentDescription(dotsIndicator: WormDotsIndicator, selectedPosition: Int) {
        val dotsContainer = dotsIndicator.getChildAt(0) as? View
        dotsContainer?.let {
            for (i in 0 until (it as ViewGroup).childCount) {
                val dot = it.getChildAt(i) as? ImageView
                dot?.contentDescription = if (i == selectedPosition) {
                    "현재 페이지: ${i + 1}"
                } else {
                    "페이지 ${i + 1}로 이동"
                }
            }
        }
    }

}
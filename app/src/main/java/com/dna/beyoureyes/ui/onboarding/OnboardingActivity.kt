package com.dna.beyoureyes.ui.onboarding

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.viewpager.widget.ViewPager
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.ActivityOnboardingBinding
import com.dna.beyoureyes.ui.assign.AssignActivity
import com.dna.beyoureyes.ui.assign.AssignMode

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // 레이아웃 바인딩
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager = binding.registerViewpager
        val dotsIndicator = binding.dotsIndicator

        // 개인정보처리방침 확인하기
        binding.privacyPolicy.setOnClickListener{
            val policyLink = "https://polar-wolf-52e.notion.site/3ff38ff000ef47b99fa8d4e890a6b4a7?pvs=74"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(policyLink))
            startActivity(intent)
        }

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
            intent.putExtra("EDIT_MODE", AssignMode.REGISTER)
            startActivity(intent)
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
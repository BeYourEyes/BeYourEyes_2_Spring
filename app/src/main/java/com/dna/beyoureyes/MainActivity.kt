package com.dna.beyoureyes

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dna.beyoureyes.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 레이아웃 바인딩
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Log.d("Chip", "isStarted")
        //startActivity(Intent(this, ResultEatActivity::class.java))
        binding.chipTest.setOnCheckedChangeListener{ _, isChanged ->
            if(isChanged) {
                binding.chipTest.chipStrokeWidth = 2f.toDp(this)
                val displayMetrics = resources.displayMetrics
                val densityDpi = displayMetrics.densityDpi
                println("Current screen density (DPI): $densityDpi")
            } else {
                binding.chipTest.chipStrokeWidth = 0f
            }
        }

        val resultFragment = ResultNutriBarFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.testBarChart, resultFragment)
            .commitNow()

        supportFragmentManager.executePendingTransactions() // 프래그먼트 실행 후 실행

         */

        // 하단 네비게이션 뷰 초기화
        val navView: BottomNavigationView = binding.navView

        // 프래그먼트 뷰에 네비게이션 컨트롤러 연결
        // -> 네비게이션 파일에 설정한대로 처리할 수 있게
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

    }

    private fun Float.toDp(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    }

}
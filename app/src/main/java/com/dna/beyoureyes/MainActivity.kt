package com.dna.beyoureyes

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dna.beyoureyes.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // OpenCV 초기화
        if (!OpenCVLoader.initDebug()) {
            Log.e("CV2TEST", "OpenCV initialization failed.")
        } else {
            Log.d("CV2TEST", "OpenCV initialization succeeded.")

            // OpenCV 기능 테스트
            testOpenCVFunction()
        }
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


    }

    private fun testOpenCVFunction() {
        // 3x3 크기의 CV_8UC1 타입의 Mat 객체 생성
        val mat = Mat(3, 3, CvType.CV_8UC1)
        Log.d("CV2TEST", "Created Mat object: Rows=${mat.rows()}, Cols=${mat.cols()}, Type=${mat.type()}")

        // Mat 객체를 이용한 기본적인 작업 예제
        mat.put(0, 0, byteArrayOf(1, 2, 3))
        Log.d("CV2TEST", "Mat values: ${mat.get(0, 0).contentToString()}")
    }

    private fun Float.toDp(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    }
}
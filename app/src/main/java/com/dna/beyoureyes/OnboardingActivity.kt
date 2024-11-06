package com.dna.beyoureyes

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class OnboardingActivity : AppCompatActivity() {

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private var isImageView1Clicked = false
    private var isImageView2Clicked = false
    private var isImageView3Clicked = false
    private var isImageView4Clicked = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        imageView3 = findViewById(R.id.imageView3)
        imageView4 = findViewById(R.id.imageView4)

        imageView1.setOnClickListener {
            isImageView1Clicked = !isImageView1Clicked
            if (isImageView1Clicked) {
                imageView1.setBackgroundResource(R.drawable.step4_1_click)
            } else {
                imageView1.setBackgroundResource(R.drawable.step4_1)
            }
        }

        imageView2.setOnClickListener {
            isImageView2Clicked = !isImageView2Clicked
            if (isImageView2Clicked) {
                imageView2.setBackgroundResource(R.drawable.step4_2_click)
            } else {
                imageView2.setBackgroundResource(R.drawable.step4_2)
            }
        }
        imageView3.setOnClickListener {
            isImageView3Clicked = !isImageView3Clicked
            if (isImageView3Clicked) {
                imageView3.setBackgroundResource(R.drawable.step4_3_click)
            } else {
                imageView3.setBackgroundResource(R.drawable.step4_3)
            }
        }
        imageView4.setOnClickListener {
            isImageView4Clicked = !isImageView4Clicked
            if (isImageView4Clicked) {
                imageView4.setBackgroundResource(R.drawable.step4_4_click)
            } else {
                imageView4.setBackgroundResource(R.drawable.step4_4)
            }
        }

    }
}
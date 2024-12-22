package com.dna.beyoureyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MyInfoEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info_edit)

        val binding = ActivityU

        val type = intent.getStringExtra("type")

        when (type) {
            "disease" -> {

            }
            "allergy" -> {

            }
        }
    }
}
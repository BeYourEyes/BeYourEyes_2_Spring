package com.dna.beyoureyes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class AssignDiseaseFragment : Fragment() {
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var imageView4: ImageView
    private var isImageView1Clicked = false
    private var isImageView2Clicked = false
    private var isImageView3Clicked = false
    private var isImageView4Clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assign_disease, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ImageView 초기화
        imageView1 = view.findViewById(R.id.imageView1)
        imageView2 = view.findViewById(R.id.imageView2)
        imageView3 = view.findViewById(R.id.imageView3)
        imageView4 = view.findViewById(R.id.imageView4)

        // 클릭 리스너 설정
        imageView1.setOnClickListener {
            isImageView1Clicked = !isImageView1Clicked
            if (isImageView1Clicked) {
                imageView1.setBackgroundResource(R.drawable.assign_disease_no_click)
            } else {
                imageView1.setBackgroundResource(R.drawable.assign_disease_no)
            }
        }

        imageView2.setOnClickListener {
            isImageView2Clicked = !isImageView2Clicked
            if (isImageView2Clicked) {
                imageView2.setBackgroundResource(R.drawable.assign_disease_diabetes_click)
            } else {
                imageView2.setBackgroundResource(R.drawable.assign_disease_diabetes)
            }
        }

        imageView3.setOnClickListener {
            isImageView3Clicked = !isImageView3Clicked
            if (isImageView3Clicked) {
                imageView3.setBackgroundResource(R.drawable.assign_disease_highblood_click)
            } else {
                imageView3.setBackgroundResource(R.drawable.assign_disease_highblood)
            }
        }

        imageView4.setOnClickListener {
            isImageView4Clicked = !isImageView4Clicked
            if (isImageView4Clicked) {
                imageView4.setBackgroundResource(R.drawable.assign_disease_hyperlipidemia_click)
            } else {
                imageView4.setBackgroundResource(R.drawable.assign_disease_hyperlipidemia)
            }
        }
    }
}
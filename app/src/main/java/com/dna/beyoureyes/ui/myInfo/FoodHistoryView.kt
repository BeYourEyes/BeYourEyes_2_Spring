package com.dna.beyoureyes.ui.myInfo

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.dna.beyoureyes.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.lang.ref.Reference
import java.text.SimpleDateFormat
import java.util.Locale

class FoodHistoryView(context: Context, attrs: AttributeSet?) :
    CardView(context, attrs) {

    // 뷰 컴포넌트들을 클래스 속성으로 정의
    private val historyImg : ImageView
    private val historyLabel : TextView
    private val kcalTextView : TextView

    init {
        // Bar View의 XML 레이아웃을 인플레이트
        LayoutInflater.from(context).inflate(R.layout.food_history_item, this, true)
        // 레이아웃 파라미터 설정
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // 레이아웃 내 뷰 초기화
        historyImg = findViewById(R.id.imageView)
        historyLabel = findViewById(R.id.label)
        kcalTextView = findViewById(R.id.kcal)


        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.FoodHistoryView)
            val kcal = typedArray.getString(R.styleable.FoodHistoryView_kcalText)
            val label = typedArray.getString(R.styleable.FoodHistoryView_historyLabel)
            val img = typedArray.getResourceId(0, -1)

            // textView 세팅
            if (kcal != null)
                kcalTextView.text = kcal+"kcal"            // 섭취 칼로리 text
            if (label != null)
                historyLabel.text = label           // 식품 기록 제목 text
            if(img != -1)
                historyImg.setImageResource(img)    // 식품 사진 image view

            typedArray.recycle()
        }
    }

    fun setData(timestamp: Timestamp?, kcal:Int?, imgUri: Uri?) {
        // textView 세팅
        kcalTextView.text = kcal?.let { it.toString()+"kcal" }?: run { "" } // 섭취 칼로리 text
        historyLabel.text = timestamp?.let { getTimeText(it) }?: run { "" } // 식품 기록 제목 text
        imgUri?.let { // 식품 사진 image view
            Glide.with(this)
                .load(it)
                .centerCrop() // 이미지를 크롭
                .into(historyImg)
        }
    }

    fun getTimeText(timestamp: Timestamp) : String { // 오전 HH시 mm분 형식의 텍스트 만들기
        val datetime = timestamp.toDate()
        val timeFormat = SimpleDateFormat("a H시 m분", Locale.KOREA) // 시간 형식
        return timeFormat.format(datetime)
    }
}
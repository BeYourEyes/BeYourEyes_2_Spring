package com.dna.beyoureyes.ui.myInfo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dna.beyoureyes.R

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

    fun setData(label:String, kcal:Int, imgResourceId:Int) {
        // textView 세팅
        kcalTextView.text = kcal.toString()+"kcal"      // 섭취 칼로리 text
        historyLabel.text = label                       // 식품 기록 제목 text
        historyImg.setImageResource(imgResourceId)      // 식품 사진 image view

    }
}
package com.dna.beyoureyes.ui.myInfo

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FoodHistoryItemBinding
import com.dna.beyoureyes.model.FoodHistory
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class FoodHistoryView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    // XML 레이아웃을 인플레이트하여 뷰 바인딩
    private val binding: FoodHistoryItemBinding =
        FoodHistoryItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // 레이아웃 파라미터 설정
        layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.FoodHistoryView)
            val kcal = typedArray.getString(R.styleable.FoodHistoryView_kcalText)
            val label = typedArray.getString(R.styleable.FoodHistoryView_historyLabel)
            val img = typedArray.getResourceId(0, -1)

            // textView 세팅
            if (kcal != null)
                binding.kcal.text = kcal + "kcal"            // 섭취 칼로리 text
            if (label != null)
                binding.label.text = label           // 식품 기록 제목 text
            if(img != -1)
                binding.imageView.setImageResource(img)    // 식품 사진 image view
            updateContentDescription()
            typedArray.recycle()
        }
    }

    fun setOnItemClickListener(history:FoodHistory, listener: (FoodHistory) -> Unit) {
        binding.cardView.setOnClickListener { listener(history) }
    }

    fun setData(timestamp: Timestamp?, kcal:Int?, imgUri: Uri?) {
        // textView 세팅
        binding.kcal.text = kcal?.let { it.toString()+"kcal" }?: run { "" } // 섭취 칼로리 text
        binding.label.text = timestamp?.let { getTimeText(it) }?: run { "" } // 식품 기록 제목 text
        // 식품 사진 image view
        Glide.with(this)
            .load(imgUri)
            .centerCrop() // 이미지를 크롭
            .into(binding.imageView)
        updateContentDescription()
    }

    private fun updateContentDescription() {
        binding.cardView.contentDescription = buildString {
            append("영양 기록: ")
            append("${binding.label.text}, ${binding.kcal.text} 섭취") // 기록 제목(시간), 칼로리
        }
    }

    private fun getTimeText(timestamp: Timestamp) : String { // 오전 HH시 mm분 형식의 텍스트 만들기
        val datetime = timestamp.toDate()
        val timeFormat = SimpleDateFormat("a H시 m분", Locale.KOREA) // 시간 형식
        return timeFormat.format(datetime)
    }
}
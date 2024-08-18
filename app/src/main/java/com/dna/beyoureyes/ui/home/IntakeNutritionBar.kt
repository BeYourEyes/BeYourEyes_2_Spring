package com.dna.beyoureyes.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.dna.beyoureyes.R

@SuppressLint("ResourceAsColor")
class IntakeNutritionBar(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val symbol: ImageView
    private val nutriLabel : TextView
    private val percentLabel : TextView
    private val massLabel : TextView
    private val dailyValueLabel : TextView
    private val bar : ProgressBar

    init {
        // XML 레이아웃을 인플레이트
        LayoutInflater.from(context).inflate(R.layout.custom_nutrition_intake_bar, this, true)

        // 레이아웃 내 뷰 초기화
        symbol = findViewById(R.id.symbol)
        nutriLabel = findViewById(R.id.nutriLabel)
        percentLabel = findViewById(R.id.percentValue)
        massLabel = findViewById(R.id.massValue)
        dailyValueLabel = findViewById(R.id.dailyValue)
        bar = findViewById(R.id.progressBar)

        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.NutritionIntakeBar)
            val maxValue = typedArray.getInteger(R.styleable.NutritionIntakeBar_maxValue, 0)
            val thresh = typedArray.getInteger(R.styleable.NutritionIntakeBar_cautionThresh, 0)
            val curValue = typedArray.getInteger(R.styleable.NutritionIntakeBar_curValue, 0)
            val unitText = typedArray.getString(R.styleable.NutritionIntakeBar_unit)
            val nutritionName = typedArray.getString(R.styleable.NutritionIntakeBar_nutritionName)

            if (maxValue != 0 ) {
                // max값 대비 입력값의 퍼센테이지 구하기
                val percentage = (curValue.toFloat() / maxValue.toFloat() * 100).toInt()

                // textView 세팅
                nutriLabel.text = nutritionName             // 영양소명 text 세팅
                massLabel.text = "${curValue}${unitText}"   // 섭취량(질량) text 세팅
                percentLabel.text = "${percentage}%"        // 권장량 대비 섭취량 비율(%) text 세팅
                dailyValueLabel.text = "일일 권장량 ${maxValue}${unitText}" // 일일 권장량 text 세팅

                // progressBar & symbol 세팅

                // 바의 값 설정
                bar.max = maxValue
                setProgressBarValue(curValue) // 애니메이션 효과 없이 표시
                //setProgressBarValueWithAnimation(curValue, 300) // 바 표시에 애니메이션 효과 적용

                if (curValue > thresh){ // 경고 범위에 들어오면 <-- 영양소별로 어떻게 할 지 더 고민 필요!!
                    // 경고 심볼 표시
                    symbol.setImageResource(R.drawable.ic_intake_caution_34)
                    // 바를 경고 색상으로 설정
                    bar.progressBackgroundTintList = ContextCompat.getColorStateList(bar.context, R.color.alert_50)
                    bar.progressTintList = ContextCompat.getColorStateList(bar.context, R.color.alert_400)
                }
                // 경고 범위에 들어오지 않으면 기본 세팅 대로(푸른색) 표시

                typedArray.recycle()
            }
        }
    }

    // 애니메이션을 통해 ProgressBar의 값을 변경
    fun setProgressBarValueWithAnimation(value: Int, duration: Long) {
        val animator = ObjectAnimator.ofInt(bar, "progress", (value*0.5).toInt(), value)
        animator.duration = duration // 애니메이션 시간 (밀리초)
        animator.start()
    }

    // 애니메이션 없이 ProgressBar의 값을 변경
    fun setProgressBarValue(value: Int) {
        bar.progress = value
    }
}
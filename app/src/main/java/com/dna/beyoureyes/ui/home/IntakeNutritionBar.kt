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
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.Gender
import com.dna.beyoureyes.R
import com.dna.beyoureyes.model.Nutrition

@SuppressLint("ResourceAsColor")
class IntakeNutritionBar(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    // Bar View 내 뷰 요소들 클래스 속성으로 정의
    private val symbol: ImageView
    private val nutriLabel : TextView
    private val percentLabel : TextView
    private val massLabel : TextView
    private val dailyValueLabel : TextView
    private val bar : ProgressBar

    init {
        // Bar View의 XML 레이아웃을 인플레이트
        LayoutInflater.from(context).inflate(R.layout.custom_intake_bar, this, true)
        // 레이아웃 파라미터 설정
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        // 레이아웃 내 뷰 초기화
        symbol = findViewById(R.id.symbol)
        nutriLabel = findViewById(R.id.nutriLabel)
        percentLabel = findViewById(R.id.percentValue)
        massLabel = findViewById(R.id.massValue)
        dailyValueLabel = findViewById(R.id.dailyValue)
        bar = findViewById(R.id.progressBar)

        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.IntakeBar)
            val maxValue = typedArray.getInteger(R.styleable.IntakeBar_maxValue, -1)
            var upperThresh:Int? = typedArray.getInteger(R.styleable.IntakeBar_upperThresh, -1)
            var lowerThresh:Int? = typedArray.getInteger(R.styleable.IntakeBar_lowerThresh, -1)
            val curValue = typedArray.getInteger(R.styleable.IntakeBar_curValue, -1)
            val massText = typedArray.getString(R.styleable.IntakeBar_massText)
            val dvText = typedArray.getString(R.styleable.IntakeBar_dailyValueText)
            val nutritionName = typedArray.getString(R.styleable.IntakeBar_nutritionName)

            // textView 세팅
            if (nutritionName != null)
                nutriLabel.text = nutritionName                 // 영양소명 text
            if (massText != null)
                massLabel.text = massText                       // 섭취량(질량) text
            if(dvText != null)
                dailyValueLabel.text = "일일 권장량 $dvText"     // 일일 권장량 text

            // progressBar 설정에 유효한 값이 있는지 확인
            if (maxValue != -1 && curValue != -1) {

                // max값 대비 입력값의 퍼센테이지 구하기
                val percentage = (curValue.toFloat() / maxValue.toFloat() * 100).toInt()
                // textView 세팅
                percentLabel.text = "${percentage}%"            // 권장량 대비 섭취량 비율(%) text

                // progressBar의 값 설정
                bar.max = maxValue
                setProgressBarValue(curValue) // 애니메이션 효과 없이 표시
                //setProgressBarValueWithAnimation(curValue) // 바 표시에 애니메이션 효과 적용

                if (upperThresh == -1) upperThresh = null
                if (lowerThresh == -1) lowerThresh = null

                if ( ((upperThresh != null) && (curValue > upperThresh))
                    || ((lowerThresh != null) && (curValue < lowerThresh)))
                {
                    // 경고 범위에 들어오면 경고 심볼 표시 & 바를 경고 색상으로 변경
                    symbol.setImageResource(R.drawable.ic_intake_caution_34)
                    bar.progressBackgroundTintList =
                        ContextCompat.getColorStateList(bar.context, R.color.alert_50)
                    bar.progressTintList =
                        ContextCompat.getColorStateList(bar.context, R.color.alert_400)
                }
            }

            typedArray.recycle()
        }
    }

    fun setData(nutrition : Nutrition) {
        val dailyValue = nutrition.getDailyValue()

        // max값 대비 입력값의 퍼센테이지 구하기
        val percentage = (nutrition.milligram.toFloat() / dailyValue.toFloat() * 100).toInt()

        // textView 세팅
        nutriLabel.text = nutrition.name
        massLabel.text = nutrition.massString
        percentLabel.text = "${percentage}%"
        dailyValueLabel.text = "일일 권장량 ${nutrition.getDailyValueText()}"

        // progressBar의 값 설정
        bar.max = dailyValue
        setProgressBarValue(nutrition.milligram) // 애니메이션 효과 없이 표시
        //setProgressBarValueWithAnimation(nutrition.milligram) // 바 표시에 애니메이션 효과 적용

        if ( nutrition.isInWarningRange() )
        {
            // 경고 범위에 들어오면 경고 심볼 표시 & 바를 경고 색상으로 변경
            symbol.setImageResource(R.drawable.ic_intake_caution_34)
            bar.progressBackgroundTintList =
                ContextCompat.getColorStateList(bar.context, R.color.alert_50)
            bar.progressTintList =
                ContextCompat.getColorStateList(bar.context, R.color.alert_400)
        }
    }

    // 애니메이션을 통해 ProgressBar의 표시
    fun setProgressBarValueWithAnimation(value: Int, duration: Long) {
        val animator = ObjectAnimator.ofInt(bar, "progress", (value*0.5).toInt(), value)
        animator.duration = 300 // 애니메이션 시간 (밀리초)
        animator.start()
    }

    // 애니메이션 없이 ProgressBar의 표시
    fun setProgressBarValue(value: Int) {
        bar.progress = value
    }
}
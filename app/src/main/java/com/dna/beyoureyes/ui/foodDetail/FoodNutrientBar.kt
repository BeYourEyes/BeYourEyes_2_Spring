package com.dna.beyoureyes.ui.foodDetail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dna.beyoureyes.BarChartCustomRenderer
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FoodNutrientBarBinding
import com.dna.beyoureyes.model.Nutrition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class FoodNutrientBar(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    // XML 레이아웃을 인플레이트하여 뷰 바인딩
    private val binding: FoodNutrientBarBinding =
        FoodNutrientBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // 레이아웃 파라미터 설정
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.IntakeBar)
            val maxValue = typedArray.getInteger(R.styleable.IntakeBar_maxValue, -1)
            val curValue = typedArray.getInteger(R.styleable.IntakeBar_curValue, -1)
            val massText = typedArray.getString(R.styleable.IntakeBar_massText)
            val dvText = typedArray.getString(R.styleable.IntakeBar_dailyValueText)
            val nutritionName = typedArray.getString(R.styleable.IntakeBar_nutritionName)

            // textView 세팅
            if (nutritionName != null)
                binding.nutrientName.text = nutritionName                 // 영양소명 text
            if (massText != null)
                binding.quantity.text = massText                       // 섭취량(질량) text
            if(dvText != null)
                binding.dailyValue.text = "권장섭취량 $dvText"     // 일일 권장량 text

            // Bar 설정에 유효한 값이 있는지 확인
            if (maxValue != -1 && curValue != -1) {
                // max값 대비 입력값의 퍼센테이지 구하기
                val percentage = (curValue.toFloat() / maxValue.toFloat() * 100).toInt()

                if (percentage == 0) {
                    binding.quantity.setTextColor(
                        ContextCompat.getColorStateList(context, R.color.blue_300)
                    )
                } // 함유량 0일 시 텍스트 색 연하게

                val entries = arrayListOf<BarEntry>()
                entries.add(BarEntry(0f, percentage.toFloat())) // Bar에 설정할 값 준비
                applyBarChart(context, entries, 100f) // Bar 디스플레이 설정
                updateContentDescription(percentage) // 스크린 리더용 설명 업데이트
            }

            typedArray.recycle()
        }
    }

    fun setData(nutrient: Nutrition) {
        binding.nutrientName.text = nutrient.name // 영양소명
        binding.quantity.text = nutrient.massString // 함유량 텍스트
        binding.dailyValue.text = "권장섭취량 ${nutrient.getDailyValueText()}" // 권장량 텍스트

        val percentage = nutrient.percentageOfDailyValue // 권장량 대비 함유량 % 구하기
        if (percentage == 0) { // 함유량 0일 시 텍스트 색 연하게
            binding.quantity.setTextColor(
                ContextCompat.getColorStateList(context, R.color.blue_300))
        } else {
            binding.quantity.setTextColor(
                ContextCompat.getColorStateList(context, R.color.blue_700))
        }

        val entries = arrayListOf<BarEntry>()
        entries.add(BarEntry(0f, percentage.toFloat()))// Bar에 설정할 값 준비
        applyBarChart(context, entries, 100f) // Bar 디스플레이 설정

        updateContentDescription(percentage) // 스크린 리더용 설명 업데이트
    }

    // 스크린 리더용 content description
    private fun updateContentDescription(percentage: Int) {
        binding.barLayout.contentDescription = buildString {
            append("${binding.nutrientName.text}: ${binding.quantity.text} 함유. ") // 당류 150g 함유
            append("${binding.dailyValue.text} 대비, ${percentage}%.") // 권장섭취량 ㅇㅇg 대비 ㅇ% 함유
        }
    }

    // 바 표시 설정
    private fun applyBarChart(context: Context, entries: List<BarEntry>, maximum: Float) {

        binding.barChart.description.isEnabled = false // chart 밑에 description 표시 유무
        binding.barChart.setTouchEnabled(false) // 터치 유무
        binding.barChart.legend.isEnabled = false // 레전드 삭제
        binding.barChart.setDrawBarShadow(true) // 회색 배경(그림자 효과)

        // 모서리 둥근 바 차트
        val renderer = BarChartCustomRenderer(binding.barChart, binding.barChart.animator, binding.barChart.viewPortHandler)
        renderer.setRightRadius(80f)
        renderer.setLeftRadius(80f)
        binding.barChart.renderer = renderer

        // XAxis (수평 막대 기준 왼쪽)
        binding.barChart.xAxis.isEnabled = false

        // YAxis(Left) (수평 막대 기준 위쪽)
        val axisLeft: YAxis = binding.barChart.axisLeft
        axisLeft.axisMinimum = 0f // 최솟값
        axisLeft.axisMaximum = maximum // 최댓값
        axisLeft.isEnabled = false

        // YAxis(Right) (수평 막대 기준 아래쪽)
        binding.barChart.axisRight.isEnabled = false

        // 차트 밖이 아니라 내부에 값 표시
        binding.barChart.setDrawValueAboveBar(false)

        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
        val set = BarDataSet(entries, "Nutri Intake")
        set.setDrawIcons(false)
        set.setDrawValues(true)
        set.color = ContextCompat.getColor(context, R.color.blue_500)
        set.barShadowColor = ContextCompat.getColor(context, R.color.gray_100)

        // 커스텀 퍼센트 포맷터 -> 비율을 float이 아니라 int형으로 표시
        set.valueFormatter = object : ValueFormatter() { // 값 표시 String 포맷 설정
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "%"
            }
        }

        // 3. [BarData] 보여질 데이터 구성
        val data = BarData(set)
        data.setValueTextColor(ContextCompat.getColor(context, R.color.white))
        data.setValueTextSize(14f)
        data.setValueTypeface(ResourcesCompat.getFont(context, R.font.wanted_sans_extra_bold))

        binding.barChart.setExtraOffsets(0f, 0f, 0f, 0f)
        binding.barChart.setViewPortOffsets(0f, dpToPx(context, -1f), 0f, dpToPx(context, -1f))

        // 모든 차트 및 데이터 설정 적용
        binding.barChart.animateY(300)
        binding.barChart.data = data
        binding.barChart.invalidate()

    }

    // calculate dp for unify ratio
    private fun dpToPx(context: Context, dp: Float): Float {
        val density = context.resources.displayMetrics.density
        return dp * density
    }

}
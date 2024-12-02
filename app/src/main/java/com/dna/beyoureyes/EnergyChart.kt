package com.dna.beyoureyes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dna.beyoureyes.model.Nutrition
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

//========================================================================
// 오늘의 섭취량 통계 디스플레이 요소 관련 클래스들
//========================================================================

// primary 생성자 - 액티비티 내 바 차트 객체 및 관련 텍스트뷰를 전달
class NutriIntakeBarDisplay(
    val barChart : BarChart,
    val nameTextView: TextView,
    val massTextView: TextView,
    val dvTextView : TextView) {

    fun hide(){
        barChart.visibility = View.GONE
        dvTextView.visibility = View.GONE
    }

    fun set(context: Context, nutri: Nutrition) {
        nameTextView.text = nutri.name
        massTextView.text = nutri.massString
        dvTextView.text = "권장 섭취량 " + nutri.getDailyValueText()
        val percentIntake = nutri.percentageOfDailyValue
        val entries = arrayListOf<BarEntry>()
        entries.add(BarEntry(0f, percentIntake.toFloat()))
        applyBarChart(context, entries, 100f)
    }

    // 바 표시 설정
    @SuppressLint("ResourceAsColor")
    fun applyBarChart(context: Context, entries: List<BarEntry>, maximum: Float) {

        barChart.description.isEnabled = false // chart 밑에 description 표시 유무
        barChart.setTouchEnabled(false) // 터치 유무
        barChart.legend.isEnabled = false // 레전드 삭제
        barChart.setDrawBarShadow(true) // 회색 배경(그림자 효과)

        // 모서리 둥근 바 차트
        val renderer = BarChartCustomRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        renderer.setRightRadius(80f)
        renderer.setLeftRadius(80f)
        barChart.renderer = renderer

        // XAxis (수평 막대 기준 왼쪽)
        barChart.xAxis.isEnabled = false

        // YAxis(Left) (수평 막대 기준 위쪽)
        val axisLeft: YAxis = barChart.getAxisLeft()
        axisLeft.axisMinimum = 0f // 최솟값
        axisLeft.axisMaximum = maximum // 최댓값
        axisLeft.isEnabled = false

        // YAxis(Right) (수평 막대 기준 아래쪽)
        barChart.axisRight.isEnabled = false

        // 차트 밖이 아니라 내부에 값 표시
        barChart.setDrawValueAboveBar(false)

        // 2. [BarDataSet] 단순 데이터를 막대 모양으로 표시, BarChart의 막대 커스텀
        val set = BarDataSet(entries, "Nutri Intake")
        set.setDrawIcons(false)
        set.setDrawValues(true)
        set.color = ContextCompat.getColor(context, R.color.blue_500)
        set.barShadowColor = ContextCompat.getColor(context, R.color.gray_100)

        //set.valueFormatter = PercentFormatter() // 값 표시 String 포맷 설정

        // 커스텀 퍼센트 포맷터 -> 비율을 float이 아니라 int형으로 표시
        set.valueFormatter = object : ValueFormatter() { // 값 표시 String 포맷 설정
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "%"
                //return value.toInt().toString() + "%"
            }
        }

        // 3. [BarData] 보여질 데이터 구성
        val data = BarData(set)
        data.setValueTextColor(ContextCompat.getColor(context, R.color.white))
        data.setValueTextSize(14f)
        data.setValueTypeface(ResourcesCompat.getFont(context, R.font.wanted_sans_extra_bold))

        barChart.setExtraOffsets(0f, 0f, 0f, 0f)
        barChart.setViewPortOffsets(0f, dpToPx(context, -1f), 0f, dpToPx(context, -1f))
        //barChart.setBackgroundColor(ContextCompat.getColor(context, R.color.chart_yellow)) // for checking spacings...

        // 모든 차트 및 데이터 설정 적용
        barChart.animateY(300)
        barChart.setData(data)
        barChart.invalidate()

    }
}

// PieChartSetting
class EnergyChart(private val chart : PieChart) {

    // =========================================================================
    // 생성자
    // =========================================================================

    // primary 생성자 - 탄단지 칼로리 값은 일단 0으로 초기화
    private var carbsCal = 40f
    private var proteinCal = 15f
    private var fatCal = 20f

    init {
        chart.setUsePercentValues(true)
        chart.setNoDataText("데이터가 없어 차트를 표시할 수 없어요.") // 차트 표시 실패할 때 안내 문구
    }

    // =========================================================================
    // set 메소드
    // =========================================================================

    fun hide(){
        chart.visibility = View.GONE
    }

    fun setNoDataText(text:String){
        chart.setNoDataText(text)
    }

    fun setCaloreis(carbs: Float, protein: Float, fat: Float ) {
        carbsCal = carbs
        proteinCal = protein
        fatCal = fat
    }

    // 앱 전체에서 쓰는 색상 정의 xml 활용하기 위해 context 전달 필요.
    // 액티비티 내에서 쓸 때 this로 context 전달하면 됨. ex) setChart(this)
    fun setChart(context: Context) {
        // 차트 표기 -----------------------------------------------------------------
        // data set - 단백질, 탄수화물, 지방 에너지 비율 구하기
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(carbsCal, "탄수화물"))
        entries.add(PieEntry(proteinCal, "단백질"))
        entries.add(PieEntry(fatCal, "지방"))

        // 색깔 적용
        val colors = listOf(
            ContextCompat.getColor(context, R.color.blue_500),
            ContextCompat.getColor(context, R.color.chart_yellow),
            ContextCompat.getColor(context, R.color.alert_500)
        )

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            setColors(colors) // 차트 요소별 색상 설정
            valueTextColor = Color.WHITE // 값(백분율) 표시 색상 설정
            valueTextSize = 14f // 값 크기
            valueTypeface = ResourcesCompat.getFont(context, R.font.wanted_sans_extra_bold) // 글꼴 수정

        }

        val pieData = PieData(pieDataSet)
        // 값에 사용자 정의 형식(백분율 값 + "%") 설정
        pieDataSet.valueFormatter = object : ValueFormatter() { // 값을 차트에 어떻게 표시할지
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%" // 값을 정수 형식으로 표시
            }
        }

        chart.legend.apply {
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            orientation = Legend.LegendOrientation.HORIZONTAL
            form = Legend.LegendForm.CIRCLE // 또는 LINE, SQUARE
            formToTextSpace = 10f // 형식과 텍스트 간격
            xEntrySpace = 24f // 항목과 항목 사이의 거리
            formSize = 10f // 형식의 크기
            textSize = 13f // 텍스트 크기
            typeface = ResourcesCompat.getFont(context, R.font.wanted_sans_regular)

        }

        chart.apply {
            data = pieData
            description.isEnabled = false // 차트 설명 비활성화
            isRotationEnabled = false // 차트 회전 활성화
            legend.isEnabled = true // 하단 설명 활성화
            isDrawHoleEnabled = true // 가운데 빈 구멍 활성화 비활성화 여부
            holeRadius = 0f // 가운데 빈 구멍 크기
            transparentCircleRadius = 0f // 투명한 부분 크기
            centerText = null // 가운데 텍스트 없앰
            setEntryLabelTextSize(14f) // label 글씨 크기
            setEntryLabelColor(Color.WHITE) // label 색상
            setEntryLabelTypeface(ResourcesCompat.getFont(context, R.font.wanted_sans_extra_bold))
            animateY(0, Easing.EaseInOutQuad) // 0.8초 동안 애니메이션 설정
            animate()
        }
    }
}

// calculate dp for unify ratio
fun dpToPx(context: Context, dp: Float): Float {
    val density = context.resources.displayMetrics.density
    return dp * density
}


package com.dna.beyoureyes.ui.assign

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.FragmentAssignBirthBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.google.firebase.Timestamp
import java.util.Calendar

class AssignBirthFragment : AssignFragment() {

    private lateinit var binding : FragmentAssignBirthBinding

    private val yearsDesc : Array<String> // 년도 역순(최신순) 리스트
    private val currentYear : Int // 현재 연도
    private val currentMonth : Int // 현재 월

    init {
        val currentDate = Calendar.getInstance() // 현재 날짜 가져오기
        currentYear = currentDate.get(Calendar.YEAR)
        currentMonth = currentDate.get(Calendar.MONTH) + 1
        yearsDesc = ((currentYear-100)..currentYear)
            .map { it.toString() }.reversed().toTypedArray() // 년도를 역순으로 가져오기
    }

    // lazy init
    override val questionMsg: String by lazy { getString(R.string.assign_step3_question) }
    override val announceForAccessibilityMsg: String by lazy {
        // 스크린 리더 대응 - numberPicker 조작에 대한 추가 설명
        questionMsg + "하단에서 태어난 년도와 날짜를 입력할 시, " +
                "수정창을 두번 탭하여 직접 입력하는 것도 가능하지만, " +
                "드래그로도 조작할 수 있어요."
    }
    // number picker 바인딩
    private val year : NumberPicker by lazy { binding.yearPicker }
    private val month : NumberPicker by lazy { binding.monthPicker }
    private val day : NumberPicker by lazy { binding.dayPicker }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignBirthBinding.inflate(inflater, container, false)

        // 스크롤로 순환할 수 있는 기능 막기
        year.wrapSelectorWheel = false
        month.wrapSelectorWheel = false
        day.wrapSelectorWheel = false

        // 년도 범위 설정: 100년 전 ~ 현재
        year.minValue = 0
        year.maxValue = 99

        // 월 범위 설정: 1월 ~ 12월
        month.minValue = 1
        month.maxValue = 12

        year.displayedValues = yearsDesc

        // number picker에 생월을 표시할 때 'x월'이라는 형식으로 표시
        month.displayedValues = getDisplayValues(1, 12, getString(R.string.assign_birth_month))

        // 일 범위 설정: 1일 부터 시작하며, 년도와 월마다 최대 일수가 다름
        day.minValue = 1
        day.maxValue = getDaysInMonth(currentYear, currentMonth)

        // 입력 년도가 넘어가며 월이 바뀔 때 최대 일수 변경되어야 함
        year.setOnValueChangedListener { _, _, input ->
            val maxDayValue = getDaysInMonth(input, month.value)
            day.maxValue = maxDayValue
        }
        // 입력 생월이 바뀔 때 최대 일수 변경되어야 함
        month.setOnValueChangedListener { _, _, input ->
            val maxDayValue = getDaysInMonth(year.value, input)
            day.maxValue = maxDayValue
        }

        // 초기값 설정
        year.value = 0
        month.value = 1
        day.value = 1

        return binding.root
    }

    // 유효성 검사 & 입력값 getter
    override fun getValidInput(): String {
        val selectedYear = yearsDesc[year.value] // 역순으로 설정된 displayedValues 사용
        val selectedMonth = month.value.toString().padStart(2, '0') // 1 -> "01"로 변환
        val selectedDay = day.value.toString().padStart(2, '0')     // 1 -> "01"로 변환
        val birth = selectedYear + selectedMonth + selectedDay
        return birth
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    // 특정 년도, 특정 월의 날짜 수 구하기
    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getDisplayValues(start: Int, end: Int, suffix: String): Array<String> {
        val displayValues = mutableListOf<String>()

        for (value in start..end) {
            displayValues.add("${value}${suffix}")
        }
        return displayValues.toTypedArray()
    }

}
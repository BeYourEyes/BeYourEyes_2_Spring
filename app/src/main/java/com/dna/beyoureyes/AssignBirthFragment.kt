package com.dna.beyoureyes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.dna.beyoureyes.databinding.FragmentAssignBirthBinding
import com.dna.beyoureyes.ui.CustomToolbar
import com.dna.beyoureyes.ui.FragmentNavigationListener
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AssignBirthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssignBirthFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentAssignBirthBinding
    private var birth = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignBirthBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        // number picker 바인딩
        val year : NumberPicker = binding.yearPicker
        val month : NumberPicker = binding.monthPicker
        val day : NumberPicker = binding.dayPicker

        // 현재 날짜 가져오기
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1

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

        // 년도를 역순으로 표시
        val yearsDesc = ((currentYear-100)..currentYear).map { it.toString() }.reversed().toTypedArray()
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

        val listener = activity as? FragmentNavigationListener
        binding.nextBtn.setOnClickListener {
            val selectedYear = yearsDesc[year.value] // 역순으로 설정된 displayedValues 사용
            val selectedMonth = month.value.toString().padStart(2, '0') // 1 -> "01"로 변환
            val selectedDay = day.value.toString().padStart(2, '0')     // 1 -> "01"로 변환
            birth = selectedYear + selectedMonth + selectedDay
            listener?.onBirthInputRecieved(birth)
            listener?.onBtnClick(this, true)
        }
        binding.toolbar.backButtonClickListener = object : CustomToolbar.ButtonClickListener {
            override fun onClicked() {
                listener?.onBtnClick(this@AssignBirthFragment, false)
            }
        }
        return binding.root
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AssignBirthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssignBirthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
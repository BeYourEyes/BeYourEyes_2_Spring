package com.dna.beyoureyes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.dna.beyoureyes.R
import com.dna.beyoureyes.databinding.ToolbarBinding


class CustomToolbar(context: Context, attrs: AttributeSet?) :
    Toolbar(context, attrs) {

    // 커스텀 툴바 View의 XML 레이아웃을 인플레이트하여 뷰 바인딩
    private val binding: ToolbarBinding =
        ToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    interface ButtonClickListener {
        fun onClicked()
    }

    var backButtonClickListener: ButtonClickListener? = null
    var skipButtonClickListener: ButtonClickListener? = null
    var closeButtonClickListener: ButtonClickListener? = null

    init {
        // start와 end 인셋을 0으로 설정. 레이아웃에서 직접 설정한 margin 외 추가 여백이 생기지 않도록 하기 위함.
        setContentInsetsRelative(0, 0)

        // attrs 입력값 속성 처리
        attrs?.let{
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomToolbar)
            val title = typedArray.getString(R.styleable.CustomToolbar_title)
            val skipEnabled =
                typedArray.getBoolean(R.styleable.CustomToolbar_skipEnabled, false)
            val backEnabled =
                typedArray.getBoolean(R.styleable.CustomToolbar_backEnabled, true) // 기본값 true
            val closeEnabled =
                typedArray.getBoolean(R.styleable.CustomToolbar_closeEnabled, false)

            // textView 세팅
            if (title != null)
                binding.title.text = title

            // 뒤로가기 버튼 세팅
            if (!backEnabled){
                binding.back.visibility = View.GONE
            }else{
                binding.back.visibility = View.VISIBLE
            }

            // 건너뛰기 버튼 세팅
            if (!skipEnabled){
                binding.skip.visibility = View.GONE
            }else{
                binding.skip.visibility = View.VISIBLE
            }

            // 닫기 버튼 세팅
            if (!closeEnabled){
                binding.close.visibility = View.GONE
            }else{
                binding.close.visibility =View.VISIBLE
            }

            typedArray.recycle()
        }

        // 뒤로 가기 버튼 터치 시 동작
        binding.back.setOnClickListener {
            backButtonClickListener?.onClicked() // 리스너 연결
        }

        // 건너뛰기 버튼 터치 시 동작
        binding.skip.setOnClickListener {
            skipButtonClickListener?.onClicked()  // 리스너 연결
        }

        // 닫기 버튼 터치 시 동작
        binding.close.setOnClickListener {
            closeButtonClickListener?.onClicked() // 리스너 연결
        }
    }

    fun setSkipButtonButtonVisibility(isVisible: Boolean) {
        binding.skip.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
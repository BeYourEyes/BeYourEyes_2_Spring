package com.dna.beyoureyes.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dna.beyoureyes.R


class CustomToolbar(context: Context, attrs: AttributeSet?) :
    Toolbar(context, attrs) {

    // 뷰 컴포넌트들을 클래스 속성으로 정의
    private val backButton : ImageButton
    private val closeButton : ImageButton
    private val skipButton : AppCompatButton
    private val titleView : TextView

    interface BackButtonClickListener {
        fun onBackButtonClicked()
    }

    var backButtonClickListener: BackButtonClickListener? = null
    // var skipButtonClickListener: BackButtonClickListener? = null
    // var closeButtonClickListener: BackButtonClickListener? = null

    init {
        // 커스텀 툴바 View의 XML 레이아웃을 인플레이트
        LayoutInflater.from(context).inflate(R.layout.toolbar, this, true)

        // start와 end 인셋을 0으로 설정. 레이아웃에서 직접 설정한 margin 외 추가 여백이 생기지 않도록 하기 위함.
        setContentInsetsRelative(0, 0)

        // 레이아웃 내 뷰 초기화
        titleView = findViewById(R.id.title)
        backButton = findViewById(R.id.back)
        skipButton = findViewById(R.id.skip)
        closeButton = findViewById(R.id.close)

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
                titleView.text = title

            // 뒤로가기 버튼 세팅
            if (!backEnabled){
                backButton.visibility = View.GONE
            }else{
                backButton.visibility = View.VISIBLE
            }

            // 건너뛰기 버튼 세팅
            if (!skipEnabled){
                skipButton.visibility = View.GONE
            }else{
                skipButton.visibility = View.VISIBLE
            }

            // 닫기 버튼 세팅
            if (!closeEnabled){
                closeButton.visibility = View.GONE
            }else{
                closeButton.visibility =View.VISIBLE
            }

            typedArray.recycle()
        }

        // 뒤로 가기 버튼 터치 시 동작
        backButton.setOnClickListener {
            backButtonClickListener?.onBackButtonClicked() // 리스너 연결
        }

        // 닫기 버튼 터치 시 동작
        closeButton.setOnClickListener {
            findNavController().popBackStack()
            // closeButtonClickListener?.onBackButtonClicked()  // 리스너 연결
        }


    }
}
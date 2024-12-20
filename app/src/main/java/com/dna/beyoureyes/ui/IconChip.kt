package com.dna.beyoureyes.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.dna.beyoureyes.R
import com.dna.beyoureyes.model.Disease
import com.google.android.material.chip.Chip

class IconChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val chip: Chip
    private val textView: TextView

    var isChecked: Boolean
        get() = chip.isChecked
        set(value) {
            chip.isChecked = value
        }

    var isCheckable: Boolean
        get() = chip.isCheckable
        set(value) {
            chip.isCheckable = value
        }

    init {
        // 레이아웃 inflate
        val view = LayoutInflater.from(context).inflate(R.layout.custom_icon_chip, this, true)

        // 내부 뷰 참조 가져오기
        chip = view.findViewById(R.id.innerChip)
        textView = view.findViewById(R.id.innerTextView)

        chip.setTextAppearance(R.style.IconChipStyle)

        // XML 레이아웃에서 전달받은 attrs 값 파싱
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DiseaseChip)
        val chipSize = typedArray.getDimensionPixelSize(R.styleable.DiseaseChip_chipSize, 98)
        val chipIconSize = typedArray.getDimensionPixelSize(R.styleable.DiseaseChip_chipIconSize, 55)
        val text = typedArray.getString(R.styleable.DiseaseChip_android_text)
        val textSize = typedArray.getDimension(R.styleable.DiseaseChip_android_textSize, 0f)
        val chipIcon = typedArray.getDrawable(R.styleable.DiseaseChip_chipIcon)
        val checkable = typedArray.getBoolean(R.styleable.DiseaseChip_android_checkable, false)
        val checked = typedArray.getBoolean(R.styleable.DiseaseChip_android_checked, false)
        typedArray.recycle()

        // 칩 설정
        setChipIconAttrs(chipSize, chipIconSize, chipIcon) // 칩 크기, 아이콘 크기, 아이콘 drawable 설정
        isCheckable = checkable // 체크 가능 여부 설정
        isChecked = checked // 체크 여부 설정

        // 텍스트뷰 설정
        val textSizeSP = textSize / context.resources.displayMetrics.scaledDensity // sp 단위 변환
        setTextAttrs(text, textSizeSP) // 텍스트 속성 설정

        // chip의 상태 변화를 TextView와 연동
        chip.setOnCheckedChangeListener { _, isChecked ->
            setTextColor()
        }
    }

    fun setCheckableDiseaseChip(disease: Disease) {
        // 체크 가능 아이콘 칩용 dimens 값 로드
        val chipSize = // 98dp(칩 크기)
            resources.getDimensionPixelSize(R.dimen.checkable_icon_chip_size)
        val iconSize = // 55dp(아이콘 크기)
            resources.getDimensionPixelSize(R.dimen.checkable_icon_chip_drawable_size)
        val drawable = AppCompatResources.getDrawable(context, disease.drawableId)
        val textSize = // 16sp(텍스트 크기)
            resources.getDimension(R.dimen.checkable_icon_chip_text_size) /
                    context.resources.displayMetrics.scaledDensity // sp 단위 변환

        isCheckable = true // 체크 가능
        setChipIconAttrs(chipSize, iconSize, drawable)
        setTextAttrs(disease.displayName, textSize)
    }

    fun setNotCheckableDiseaseChip(disease: Disease) {
        // 체크 가능 아이콘 칩용 dimens 값 로드
        val chipSize = // 84dp(칩 크기)
            resources.getDimensionPixelSize(R.dimen.icon_chip_size)
        val iconSize = // 47dp(아이콘 크기)
            resources.getDimensionPixelSize(R.dimen.icon_chip_drawable_size)
        val drawable = AppCompatResources.getDrawable(context, disease.drawableId)
        val textSize = // 14sp(텍스트 크기)
            resources.getDimension(R.dimen.icon_chip_text_size) /
                    context.resources.displayMetrics.scaledDensity // sp 단위 변환

        isCheckable = false // 체크 불가능
        setChipIconAttrs(chipSize, iconSize, drawable)
        setTextAttrs(disease.displayName, textSize)
        chip.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO // 스크린 리더 중복 읽기 방지
    }


    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        chip.setOnCheckedChangeListener{ buttonView, isChecked ->
            listener.onCheckedChanged(buttonView, isChecked) // listener 실행
            setTextColor()
        }
    }

    private fun setChipIconAttrs(chipSize: Int, iconSize: Int, chipIcon: Drawable?) {
        chip.apply{
            layoutParams = LayoutParams(chipSize, chipSize)
        }
        chip.chipStartPadding = getChipIconPadding(chipSize, iconSize)
        chip.chipIcon = chipIcon // 칩 아이콘 drawable
        chip.chipIconSize = iconSize.toFloat() // 칩 크기
    }

    private fun setTextAttrs(text:String?, textSize: Float) {
        chip.contentDescription = text
        textView.text = text
        textView.textSize = textSize
        setTextColor()
    }

    private fun getChipIconPadding(chipSize:Int, chipIconSize:Int): Float {
        return (chipSize - chipIconSize).toFloat()/2f
    }

    private fun setTextColor() {
        if (isCheckable){
            if (isChecked)
                textView.setTextAppearance(R.style.IconChipText_Checked) // 체크될 시
            else
                textView.setTextAppearance(R.style.IconChipText_Unchecked) // 체크 안됐을 시
        }else {
            textView.setTextAppearance(R.style.IconChipText) // 체크 비활성화 시
        }
    }
}
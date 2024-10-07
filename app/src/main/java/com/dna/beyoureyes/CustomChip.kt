package com.dna.beyoureyes

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView

class CustomChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){

    private lateinit var chipTextView: TextView
    private var paint : Paint = Paint()

    init {
        LayoutInflater.from(context).inflate(R.layout.chip_allergy_result, this, true)
        chipTextView = findViewById(R.id.chipText)
        background = context.getDrawable(R.drawable.allergy_chip)

        setOnClickListener{
            toggleSelection()
        }

    }
    fun setChipText(text: String){
        chipTextView.text = text
    }

    fun toggleSelection() {
        isSelected = !isSelected
        updateBackGroundcolor()
    }

    private fun updateBackGroundcolor() {
        background = if (isSelected) {
            context.getDrawable(R.drawable.allergy_button_checked_background)
        } else {
            context.getDrawable(R.drawable.allergy_button_default_background)
        }
    }

}
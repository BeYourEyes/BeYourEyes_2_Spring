package com.dna.beyoureyes.model

import android.widget.ImageView
import com.google.android.material.chip.Chip

data class diseaseInfo (
    val name: String,
    val button: ImageView,
    var isClicked: Boolean = false,
    val normalImage: Int,
    val clickedImage: Int
){
}
package com.dna.beyoureyes.model

import android.widget.ImageView

data class diseaseInfo (
    val name: String,
    val button: ImageView,
    var isClicked: Boolean = false,
    val normalImage: Int,
    val clickedImage: Int
){
}

data class allergyInfo (
    val name: String,
    val isClicked: Boolean = false
) {
}
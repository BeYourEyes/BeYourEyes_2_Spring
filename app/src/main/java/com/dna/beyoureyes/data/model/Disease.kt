package com.dna.beyoureyes.data.model

import com.dna.beyoureyes.R

enum class Disease(val displayName: String, val drawableId: Int) {
    DIABETES("당뇨", R.drawable.ic_disease_diabetes_55),
    HYPERTENSION("고혈압", R.drawable.ic_disease_high_blood_55),
    HYPERLIPIDEMIA("고지혈증", R.drawable.ic_disease_hyperlipidemia_55)
}
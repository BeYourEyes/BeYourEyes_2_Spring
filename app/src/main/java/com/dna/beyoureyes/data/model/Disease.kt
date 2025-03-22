package com.dna.beyoureyes.data.model

import com.dna.beyoureyes.R

enum class Disease(val displayName: String, val drawableId: Int) {
    DIABETES("당뇨", R.drawable.ic_disease_diabetes_55),
    HYPERTENSION("고혈압", R.drawable.ic_disease_high_blood_55),
    HYPERLIPIDEMIA("고지혈증", R.drawable.ic_disease_hyperlipidemia_55);

    // Disease Set을 DB 요청 형식에 맞춰 Map으로 변환하는 메소드 정의
    companion object {
        fun MutableSet<Disease>?.toMap(): Map<String, Boolean> {
            return Disease.values().associate { disease ->
                disease.name.lowercase() to (this?.contains(disease) == true)
            }
        }
    }
}
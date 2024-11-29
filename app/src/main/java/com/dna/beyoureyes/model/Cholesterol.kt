package com.dna.beyoureyes.model

import com.google.firebase.firestore.DocumentSnapshot

class Cholesterol(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = milligram.toString() + "mg"

    override fun getDailyValue(): Int
            = Companion.getDailyValue()
    override fun getDailyValueText(): String
            = Companion.getDailyValueText()
    override fun isInWarningRange(): Boolean
            = Companion.isInWarningRange(milligram)
    override fun fromFirestore(document: DocumentSnapshot): Nutrition?
            = Companion.fromFirestore(document)


    // static 영역
    companion object {
        const val NAME = "콜레스테롤"
        const val DB_FIELD_NAME = "chol"
        fun getDailyValue() : Int {
            /*
            // 고지혈증 및 고혈압 유무에 따른 섭취기준 조절 적용할 것인가? 더 고민 후 적용해보기
            val disease = AppUser.info?.disease
            if( disease != null
                && (disease.contains("hyperlipidemia") || disease.contains("highblood"))){
                return 200
            } else {
                return 300 // 일반적으로는 300mg 미만 섭취 권장
            }
             */
            return 300 // 300mg 미만 섭취 권장
        }
        fun getDailyValueText() : String {
            return getDailyValue().toString() + "mg 미만"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return getDailyValue() <= milligram
        }
        fun fromFirestore(document: DocumentSnapshot): Cholesterol? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Cholesterol(it) }
        }
    }
}
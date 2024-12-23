package com.dna.beyoureyes.model

import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat

class Carbs(override var milligram: Int = 0) : CaloricNutrient {

    // 인스턴스 영역
    override val name: String = NAME
    override val dbFiledName: String = DB_FIELD_NAME
    override val massString: String
        get() = DecimalFormat("#.##g").format(milligram/1000.0)
    override val kcal: Int get() = (milligram / 1000) * KCAL_PER_GRAM

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
        const val NAME = "탄수화물"
        const val DB_FIELD_NAME = "carbs"
        const val KCAL_PER_GRAM = 4

        fun getDailyValue() : Int {
            return 130 * 1000 // 성별, 나이 무관 권장 섭취량 130g
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram < 100 * 1000  // 성별, 나이 무관 평균 필요량 100g (최소한의 섭취 기준)
        }
        fun fromFirestore(document: DocumentSnapshot): Carbs? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Carbs(it) }
        }
    }
}
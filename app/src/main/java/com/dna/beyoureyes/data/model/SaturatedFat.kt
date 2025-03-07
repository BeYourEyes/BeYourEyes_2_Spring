package com.dna.beyoureyes.data.model

import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat

class SaturatedFat(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val dbFiledName: String = DB_FIELD_NAME
    override val massString: String
        get() = DecimalFormat("#.##g").format(milligram/1000.0)

    override fun getDailyValue(): Int
            = Companion.getDailyValue()
    override fun getDailyValueText(): String
            = Companion.getDailyValueText()
    override fun isInWarningRange(): Boolean
            = isInWarningRange(milligram)
    override fun fromFirestore(document: DocumentSnapshot): Nutrition?
            = Companion.fromFirestore(document)


    // static 영역
    companion object {
        const val NAME = "포화지방"
        const val DB_FIELD_NAME = "satFat"
        fun getDailyValue() : Int {
            // 사용자 일일 권장 칼로리 구하기. 기본값은 2000kcal
            val dailyEnergy = AppUser.info?.getDailyEnergyRequirement() ?:2000
            return ((dailyEnergy * 0.07) / 9).toInt() * 1000  // 일일 권장 칼로리의 7% 이내
        }
        fun getDailyValueText() : String {
            return (getDailyValue() /1000).toString() + "g 이내"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram > getDailyValue()
        }
        fun fromFirestore(document: DocumentSnapshot): SaturatedFat? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { SaturatedFat(it) }
        }
    }
}
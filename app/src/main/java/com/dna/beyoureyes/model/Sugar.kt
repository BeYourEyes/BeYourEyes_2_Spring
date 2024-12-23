package com.dna.beyoureyes.model

import com.dna.beyoureyes.AppUser
import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat

class Sugar(override var milligram: Int = 0) : Nutrition {

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
            = Companion.isInWarningRange(milligram)
    override fun fromFirestore(document: DocumentSnapshot): Nutrition?
            = Companion.fromFirestore(document)


    // static 영역
    companion object {
        const val NAME = "당류"
        const val DB_FIELD_NAME = "sugar"
        fun getDailyValue() : Int {
            // 사용자 일일 권장 칼로리 구하기. 기본값은 2000kcal
            val dailyEnergy = AppUser.info?.getDailyEnergyRequirement() ?:2000
            return ((dailyEnergy * 0.1) / 4).toInt() * 1000  // 일일 권장 칼로리의 10% (10~20%를 권장)
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram > getDailyValue()*2  // 일일 권장 칼로리의 20% 이내인지 검사
        }
        fun fromFirestore(document: DocumentSnapshot): Sugar? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Sugar(it) }
        }
    }
}
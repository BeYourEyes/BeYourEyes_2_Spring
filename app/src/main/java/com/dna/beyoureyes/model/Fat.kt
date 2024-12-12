package com.dna.beyoureyes.model

import com.dna.beyoureyes.AppUser
import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat

class Fat(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = DecimalFormat("#.##g").format(milligram.toDouble()/1000)

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
        const val NAME = "지방"
        const val DB_FIELD_NAME = "fat"
        fun getDailyValue() : Int {
            // 사용자 일일 권장 칼로리 구하기. 기본값은 2000kcal
            val dailyEnergy = AppUser.info?.getDailyEnergyRequirement() ?:2000
            /*
            // 고지혈증 유무에 따른 섭취기준 조절 적용할 것인가? 더 고민 후 적용해보기
            val disease = AppUser.info?.disease
            if( disease != null && (disease.contains("hyperlipidemia")){
                return ((energyReq * 0.2) / 9).toInt() * 1000 // 전체 칼로리의 20%
            } else {
                return ((energyReq * 0.3) / 9).toInt() * 1000 // 전체 칼로리의 30%
            }
             */
            return (( dailyEnergy * 0.3) / 9).toInt() * 1000 // 일일 권장 칼로리의 30%
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram > getDailyValue() // 권장 범위를 초과하는지 검사
        }
        fun fromFirestore(document: DocumentSnapshot): Fat? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Fat(it) }
        }
    }
}
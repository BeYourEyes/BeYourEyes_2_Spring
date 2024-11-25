package com.dna.beyoureyes.model

import com.dna.beyoureyes.AppUser

class Carbs(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = (milligram/1000).toString() + "g"

    override fun getDailyValue(): Int
        = Companion.getDailyValue()
    override fun getDailyValueText(): String
        = Companion.getDailyValueText()
    override fun isInWarningRange(): Boolean
        = Companion.isInWarningRange(milligram)


    // static 영역
    companion object {
        const val NAME = "탄수화물"
        const val DB_FIELD_NAME = "carbs"
        fun getDailyValue() : Int {
            return 130 * 1000 // 성별, 나이 무관 권장 섭취량 130g
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram < 100 * 1000  // 성별, 나이 무관 평균 필요량 100g (최소한의 섭취 기준)
        }
    }
}
package com.dna.beyoureyes.model

class SaturatedFat(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = (milligram/1000).toString() + "g"

    override fun getDailyValue(age: Int, isMan: Boolean): Int
            = Companion.getDailyValue()
    override fun getDailyValueText(age: Int, isMan: Boolean): String
            = Companion.getDailyValueText()
    override fun isInWarningRange(age: Int, isMan: Boolean): Boolean
            = Companion.isInWarningRange(milligram)


    // static 영역
    companion object {
        const val NAME = "포화지방"
        fun getDailyValue() : Int {
            return 15 * 1000
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram > 15 * 1000
        }
    }
}
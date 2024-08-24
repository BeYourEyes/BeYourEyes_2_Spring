package com.dna.beyoureyes.model

class Cholesterol(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = milligram.toString() + "mg"

    override fun getDailyValue(age: Int, isMan: Boolean): Int
            = Companion.getDailyValue()
    override fun getDailyValueText(age: Int, isMan: Boolean): String
            = Companion.getDailyValueText()
    override fun isInWarningRange(age: Int, isMan: Boolean): Boolean
            = Companion.isInWarningRange(milligram)


    // static 영역
    companion object {
        const val NAME = "콜레스테롤"
        fun getDailyValue() : Int {
            return 300
        }
        fun getDailyValueText() : String {
            return getDailyValue().toString() + "mg 미만"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return getDailyValue() <= milligram
        }
    }
}
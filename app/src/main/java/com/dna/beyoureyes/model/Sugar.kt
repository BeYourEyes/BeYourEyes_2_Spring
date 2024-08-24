package com.dna.beyoureyes.model

class Sugar(override var milligram: Int = 0) : Nutrition {

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
        const val NAME = "당류"
        fun getDailyValue() : Int {
            return 100 * 1000
        }
        fun getDailyValueText() : String {
            return (getDailyValue()/1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return milligram > 100 * 1000
        }
    }
}
package com.dna.beyoureyes.model

class Natrium(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = milligram.toString() + "mg"

    override fun getDailyValue(age: Int, isMan: Boolean): Int
            = Companion.getDailyValue(age)
    override fun getDailyValueText(age: Int, isMan: Boolean): String
            = Companion.getDailyValueText(age)
    override fun isInWarningRange(age: Int, isMan: Boolean): Boolean
            = Companion.isInWarningRange(milligram, age)


    // static 영역
    companion object {
        const val NAME = "나트륨"
        fun getDailyValue(age: Int) : Int {
            val upper = when(age){
                in 15..64 -> 2300
                in 65..74 -> 2100
                in 75..Int.MAX_VALUE -> 1700
                else -> 2300 // 14세 이하?
            }
            return upper
        }
        fun getDailyValueText(age: Int) : String {
            return getDailyValue(age).toString() + "mg"
        }
        fun isInWarningRange(milligram: Int, age: Int): Boolean {
            return getDailyValue(age) < milligram
        }
    }
}
package com.dna.beyoureyes.model

class Protein(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val massString: String
        get() = (milligram/1000).toString() + "g"

    override fun getDailyValue(age: Int, isMan: Boolean): Int
            = Companion.getDailyValue(age, isMan)
    override fun getDailyValueText(age: Int, isMan: Boolean): String
            = Companion.getDailyValueText(age, isMan)
    override fun isInWarningRange(age: Int, isMan: Boolean): Boolean
            = Companion.isInWarningRange(milligram, age, isMan)


    // static 영역
    companion object {
        const val NAME = "단백질"
        fun getDailyValue(age: Int, isMan: Boolean) : Int {
            if (isMan) { // 남성의 경우
                val dv = when(age){
                    in 15..49 -> 65 * 1000
                    in 50..Int.MAX_VALUE -> 60 * 1000
                    else -> 65 * 1000 // 14세 이하?
                }
                return dv
            }else{      // 여성의 경우
                val dv = when(age){
                    in 15..29 -> 55 * 1000
                    in 30..Int.MAX_VALUE -> 50 * 1000
                    else -> 55 * 1000 // 14세 이하?
                }
                return dv
            }
        }
        fun getDailyValueText(age: Int, isMan: Boolean) : String {
            return (getDailyValue(age, isMan) /1000).toString() + "g"
        }
        fun isInWarningRange(milligram: Int, age: Int, isMan: Boolean): Boolean {
            if (isMan) {                                    // 남성의 경우
                val lower = when(age){
                    in 15..18 -> 55 * 1000
                    in 19..Int.MAX_VALUE -> 50 * 1000
                    else -> 55 * 1000 // 14세 이하?
                }
                return lower > milligram
            }else{                                          // 여성의 경우
                val lower = when(age){
                    in 15..29 -> 45 * 1000
                    in 30..Int.MAX_VALUE -> 40 * 1000
                    else -> 45 * 1000 // 14세 이하?
                }
                return lower > milligram
            }
        }
    }
}
package com.dna.beyoureyes.model

import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.model.Gender
import com.google.firebase.firestore.DocumentSnapshot
import java.text.DecimalFormat

class Protein(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
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
        const val NAME = "단백질"
        const val DB_FIELD_NAME = "protein"
        fun getDailyValue() : Int {
            // 나이, 성별에 따른 권장섭취량 분류
            val age:Int = AppUser.info?.age ?:20
            if (AppUser.info?.gender == Gender.MAN.ordinal) { // 남성의 경우
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
        fun getDailyValueText() : String {
            return (getDailyValue() /1000).toString() + "g"
        }
        fun isInWarningRange(milligram:Int): Boolean {
            // 나이, 성별에 따른 최소한의 섭취량 기준 확인 -> 이보다 적은지 검사
            val age:Int = AppUser.info?.age ?:20
            if (AppUser.info?.gender == Gender.MAN.ordinal) { // 남성의 경우
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
        fun fromFirestore(document: DocumentSnapshot): Protein? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Protein(it) }
        }
    }
}
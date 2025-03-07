package com.dna.beyoureyes.data.model

import com.google.firebase.firestore.DocumentSnapshot

class Natrium(override var milligram: Int = 0) : Nutrition {

    // 인스턴스 영역
    override val name: String = NAME
    override val dbFiledName: String = DB_FIELD_NAME
    override val massString: String
        get() = milligram.toString() + "mg"

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
        const val NAME = "나트륨"
        const val DB_FIELD_NAME = "natrium"
        fun getDailyValue() : Int {
            // 상한 섭취량 = CDPR로 설정
            val age = AppUser.info?.age ?:20
            val upper = when(age){
                in 15..64 -> 2300
                in 65..74 -> 2100
                in 75..Int.MAX_VALUE -> 1700
                else -> 2300 // 14세 이하?
            }
            /*
            // 고혈압 유무에 따른 섭취기준 조절 적용할 것인가? 더 고민 후 적용해보기
            val disease = AppUser.info?.disease
            if( disease != null && disease.contains("highblood") && upper > 2000) {
                return 2000 // 고지혈증은 하루 2000mg(소금 5g) 안 넘게
            }
             */
            return upper
        }
        fun getDailyValueText() : String {
            return getDailyValue().toString() + "mg"
        }
        fun isInWarningRange(milligram: Int): Boolean {
            return getDailyValue() < milligram
        }
        fun fromFirestore(document: DocumentSnapshot): Natrium? {
            return document.getLong(DB_FIELD_NAME)?.toInt()?.let { Natrium(it) }
        }
    }
}
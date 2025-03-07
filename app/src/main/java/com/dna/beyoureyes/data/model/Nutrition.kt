package com.dna.beyoureyes.data.model

import com.google.firebase.firestore.DocumentSnapshot

interface Nutrition { // 나중에 Nutrient로 이름 변경 고려... 의미상

    val name : String       // 영양소명
    val dbFiledName: String // db 저장 시 필드명
    var milligram : Int     // 질량(mg 단위)
    val massString: String  // 단위를 포함한 질량 문자열 (ex. "10g")
    val percentageOfDailyValue:Int // 질량이 사용자 권장량 대비 몇 %인지 정수로 반환
        get() = ( milligram.toDouble() / getDailyValue().toDouble() * 100).toInt()

    fun getDailyValue() : Int          // 영양소의 일일 권장량 반환
    fun getDailyValueText() : String   // 영양소의 일일 권장량 문자열 반환
    fun isInWarningRange() : Boolean   // 영양소의 질량이 권장량 대비 경고 범위에 포함되었는지 검사

    fun fromFirestore(document: DocumentSnapshot): Nutrition? // 파이어스토어 문서 파싱하여 객체 생성

    fun scaleQuantityByFactor(factor: Double) {
        milligram = (milligram * factor).toInt()
    }
}
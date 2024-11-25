package com.dna.beyoureyes.model

interface Nutrition {

    val name : String       // 영양소명
    var milligram : Int     // 질량(mg 단위)
    val massString: String  // 단위를 포함한 질량 문자열 (ex. "10g")

    fun getDailyValue() : Int          // 영양소의 일일 권장량 반환
    fun getDailyValueText() : String   // 영양소의 일일 권장량 문자열 반환
    fun isInWarningRange() : Boolean   // 영양소의 질량이 권장량 대비 경고 범위에 포함되었는지 검사

}
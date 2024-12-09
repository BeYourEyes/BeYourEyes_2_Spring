package com.dna.beyoureyes.model

////////// OPEN API로 식품 분석 결과 가져올 클래스 /////////////
data class NutrientResult(
    val prot: Int,   // 단백질
    val fatce: Int,  // 지방
    val chocdf: Int, // 탄수화물
    val sugar: Int,  // 당류
    val nat: Int,    // 나트륨
    val chole: Int,  // 콜레스테롤
    val fasat: Int   // 포화지방산
)
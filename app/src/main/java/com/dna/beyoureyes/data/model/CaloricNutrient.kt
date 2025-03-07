package com.dna.beyoureyes.data.model

// 영양소 중 열량을 제공하는 영양성분 (탄수화물, 단백질, 지방)
interface CaloricNutrient: Nutrition {
    val kcal : Int     // 열량
}
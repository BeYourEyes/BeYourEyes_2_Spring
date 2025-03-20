package com.dna.beyoureyes.data.api.request

import com.dna.beyoureyes.data.api.model.NutritionInfo

data class FoodRecordRequest (
    val image: String, // TODO: 파일? url? 최종 확인 후 반영
    val timestamp: String,
    val foodData: NutritionInfo
)
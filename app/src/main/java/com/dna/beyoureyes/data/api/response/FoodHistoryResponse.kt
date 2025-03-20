package com.dna.beyoureyes.data.api.response

import com.dna.beyoureyes.data.api.model.NutritionInfo
import com.dna.beyoureyes.data.model.FoodHistory
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

data class FoodHistoryResponse(
    @SerializedName("log_id") val id: Int,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("food_photo") val imgRefPath: String,
    @SerializedName("nutrition_info") val nutritionInfo: NutritionInfo
) {
    // FoodHistoryResponse(API 응답 형식) -> FoodHistory(앱 내 데이터 형식)로 변환하는 메소드 정의
    companion object {
        fun FoodHistoryResponse.toFoodHistory(): FoodHistory {
            return FoodHistory(
                timestamp = try {
                    LocalDateTime.parse(
                        this.timestamp,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )
                } catch (e: Exception) {
                    throw IllegalArgumentException("Failed to convert FoodHistoryResponse:" +
                            " can't parse ${this.timestamp} to LocalDateTime")
                },
                kcal = this.nutritionInfo.kcal,
                nutritions = this.nutritionInfo.nutritionList,
                imgRefPath = this.imgRefPath
            )
        }
    }
}
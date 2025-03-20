package com.dna.beyoureyes.data.api.model

import com.dna.beyoureyes.data.model.Carbs
import com.dna.beyoureyes.data.model.Cholesterol
import com.dna.beyoureyes.data.model.Fat
import com.dna.beyoureyes.data.model.Natrium
import com.dna.beyoureyes.data.model.Nutrition
import com.dna.beyoureyes.data.model.Protein
import com.dna.beyoureyes.data.model.SaturatedFat
import com.dna.beyoureyes.data.model.Sugar
import com.google.gson.annotations.SerializedName

data class NutritionInfo(
    @SerializedName("calories") val kcal: Int,
    @SerializedName("carbohydrates") private val carbsMg: Int,
    @SerializedName("protein") private val proteinMg: Int,
    @SerializedName("fat") private val fatMg: Int,
    @SerializedName("cholesterol") private val cholesterolMg: Int,
    @SerializedName("sodium") private val natriumMg: Int,
    @SerializedName("sugar") private val sugarMg: Int,
    @SerializedName("saturatedFat") private val saturatedFatMg: Int
) {
    val nutritionList: List<Nutrition> get() = listOf(
        Natrium(natriumMg),
        Carbs(carbsMg),
        Sugar(sugarMg),
        Protein(proteinMg),
        Fat(fatMg),
        SaturatedFat(saturatedFatMg),
        Cholesterol(cholesterolMg)
    )
}
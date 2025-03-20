package com.dna.beyoureyes.data.model

import com.dna.beyoureyes.data.api.model.NutritionInfo

data class Food(
    private var _kcal:Int? = null,
    private var _nutritions: List<Nutrition>?= null,
    private var _allergy:Set<Allergen>?= null) {

    val kcal get() = _kcal
    val nutritions get() = _nutritions
    val allergy get() = _allergy

    fun scaleQuantityByFactor(factor: Double) {
        _kcal = _kcal?.let { (it * factor).toInt() }
        _nutritions?.forEach{ it.scaleQuantityByFactor(factor) }
    }

    fun setAllergyData(algSet:Set<Allergen>?) {
        _allergy = algSet
    }

    companion object{
        // Food -> NutritionInfo 변환하는 메소드 정의
        fun Food.toNutritionInfo(): NutritionInfo {
            return NutritionInfo(
                kcal = this.kcal
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'kcal'"),
                carbsMg = (this.nutritions?.find { it is Carbs } as? Carbs)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'carbohydrates'"),
                proteinMg = (this.nutritions?.find { it is Protein } as? Protein)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'protein'"),
                fatMg = (this.nutritions?.find { it is Fat } as? Fat)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'fat'"),
                cholesterolMg = (this.nutritions?.find { it is Cholesterol } as? Cholesterol)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'cholesterol'"),
                natriumMg = (this.nutritions?.find { it is Natrium } as? Natrium)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'sodium'"),
                sugarMg = (this.nutritions?.find { it is Sugar } as? Sugar)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'sugar'"),
                saturatedFatMg = (this.nutritions?.find { it is SaturatedFat } as? SaturatedFat)?.milligram
                    ?: throw IllegalArgumentException("Can't Convert Food to NutritionInfo: NO VALUE for 'saturatedFat'"),
            )
        }
    }
}
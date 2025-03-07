package com.dna.beyoureyes.data.model

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

}
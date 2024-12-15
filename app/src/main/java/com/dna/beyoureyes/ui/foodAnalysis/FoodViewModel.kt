package com.dna.beyoureyes.ui.foodAnalysis

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.model.Food


class FoodViewModel : ViewModel() {
    private val _foodData = MutableLiveData<Food>() // 사진에서 추출한 식품 데이터
    private val _capturedImageUri = MutableLiveData<Uri>()  // 촬영 원본 이미지 Uri
    private val _processedImageUri = MutableLiveData<Uri>() // OCR용 후처리한 이미지 Uri
    val foodData : LiveData<Food> = _foodData
    val capturedImageUri: LiveData<Uri> = _capturedImageUri
    val processedImageUri: LiveData<Uri> = _processedImageUri

    /*
    // foodData 라이브 데이터에 map을 활용하여 필요한 상세 정보를 아래와 같이 또 다른 라이브 데이터로 정의 가능
    // foodData의 알레르기 정보 중 유저 정보와의 교집합을 라이브 데이터로 정의한 예시.
    val allergiesToAlert : LiveData<Set<String>?> = _foodData.map { food ->
        food.allergy?.let{ AppUser.info?.findMatchingAllergy(it) }
    }
     */

    fun isCaptureSuccessful() : Boolean {
        return _capturedImageUri.value != null
    }

    fun isKcalValid() : Boolean {
        return _foodData.value?.kcal != null
    }

    fun hasAllergiesToAlert() : Boolean {
        return !(_foodData.value?.allergy?.let{ AppUser.info?.findMatchingAllergy(it) }.isNullOrEmpty())
    }

    fun isNutritionDataValid() : Boolean {
        return _foodData.value?.nutritions != null
    }
    fun isAllergyDataValid() : Boolean {
        return _foodData.value?.allergy != null
    }

    fun isFoodDataAvaiable() : Boolean {
        return _foodData.value != null
    }

    fun setCapturedImageUri(uri: Uri) {
        _capturedImageUri.value = uri
    }

    fun setProcessedImageUri(uri: Uri) {
        _processedImageUri.value = uri
    }

    fun getCapturedImageUri() : Uri? {
        return _capturedImageUri.value
    }

    fun setFoodData(food:Food) {
        _foodData.value = food
    }

    fun getFoodData() : Food? {
        return _foodData.value
    }

    fun getOcrInputUri():Uri? {
        return _processedImageUri.value
    }

}
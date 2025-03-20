package com.dna.beyoureyes.ui.myInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dna.beyoureyes.data.model.FoodHistory

class MyInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val _foodHistoryItems = MutableLiveData<MutableList<FoodHistory>>(mutableListOf())
    val foodHistoryItems: LiveData<MutableList<FoodHistory>> get() = _foodHistoryItems

    private val _selectedFoodHistory = MutableLiveData<FoodHistory?>()
    val selectedFoodHistory: LiveData<FoodHistory?> get() = _selectedFoodHistory

    // 데이터 설정 함수 (Activity에서 전달)
    fun setFoodHistory(foodHistory: MutableList<FoodHistory>) {
        _foodHistoryItems.value = foodHistory
    }

    // 선택한 음식 기록 설정
    fun selectFoodHistory(foodHistory: FoodHistory) {
        _selectedFoodHistory.value = foodHistory
    }

    fun addFoodHistory(foodHistory: FoodHistory) {
        _foodHistoryItems.value?.add(foodHistory)
    }

    // 상세 페이지에서 데이터 조회 후 초기화 (필요시)
    fun clearSelectedFoodHistory() {
        _selectedFoodHistory.value = null
    }
}
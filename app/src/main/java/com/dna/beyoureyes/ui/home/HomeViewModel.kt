package com.dna.beyoureyes.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.R
import com.dna.beyoureyes.data.model.Nutrition


class HomeViewModel: ViewModel() {
    // 사용자의 실제 칼로리 섭취량
    private val _totalKcal = MutableLiveData<Int>()
    val totalKcal: LiveData<Int> get() = _totalKcal

    // 사용자의 목표 칼로리 섭취량 (기본값 2000 kcal)
    private val _energyRequirement = MutableLiveData<Int>(2000)
    val energyRequirement: LiveData<Int> get() = _energyRequirement

    // 영양소별 섭취량 리스트
    private val _nutriIntakeItems = MutableLiveData<List<Nutrition>>(emptyList())
    val nutriIntakeItems: LiveData<List<Nutrition>> get() = _nutriIntakeItems

    // 칼로리 섭취 평가 메시지
    private val _topMsgKcal = MutableLiveData<String>()
    val topMsgKcal: LiveData<String> get() = _topMsgKcal
    private val _topMsg2 = MutableLiveData<String>()
    val topMsg2: LiveData<String> get() = _topMsg2
    private val _topMsgSmall = MutableLiveData<String>()
    val topMsgSmall: LiveData<String> get() = _topMsgSmall

    // 칼로리 섭취 평가 캐릭터 이미지 리소스 ID
    private val _topCharacterResId = MutableLiveData<Int>()
    val topCharacterResId: LiveData<Int> get() = _topCharacterResId

    // 필요한 데이터 초기화 (MainViewModel에서 가져온 데이터 세팅)
    fun setUserIntakeData(kcal: Int, nutritions: List<Nutrition>) {
        _totalKcal.value = kcal
        _nutriIntakeItems.value = nutritions.toMutableList()

        // 목표 칼로리 설정
        val userEnergyRequirement = AppUser.info?.getDailyEnergyRequirement() ?: 2000
        _energyRequirement.value = userEnergyRequirement

        // 칼로리 섭취 평가 로직 적용
        evaluateKcalIntake(kcal, userEnergyRequirement)
    }

    // 칼로리 섭취 평가 및 메시지 설정
    private fun evaluateKcalIntake(totalKcal: Int, energyRequirement: Int) {
        if (totalKcal < energyRequirement) {
            _topMsgKcal.value = "${energyRequirement - totalKcal}"
            _topMsg2.value = "적습니다"
            _topMsgSmall.value = "더 든든하게 식사하세요!"
            _topCharacterResId.value = R.drawable.home_sad
        } else {
            val diff = totalKcal - energyRequirement // 두 값 차 설정
            _topMsgKcal.value = "$diff"
            _topMsg2.value = "많습니다"
            if (diff <= 200) { // 권장량 + 200kcal까지는 적정 범위로 임의 설정.
                _topMsgSmall.value = "목표를 충족했네요! 잘 하셨어요\uD83C\uDF89"
                _topCharacterResId.value = R.drawable.home_good
            } else { // 과다 섭취 경고 범위
                _topMsgSmall.value = "과다 섭취에 주의하세요!"
                _topCharacterResId.value = R.drawable.home_sad
            }
        }
    }
}
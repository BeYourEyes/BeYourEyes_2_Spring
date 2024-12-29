package com.dna.beyoureyes.ui.assign

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease
import com.dna.beyoureyes.model.FirebaseHelper
import com.dna.beyoureyes.model.UserInfo
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Date

class AssignViewModel : ViewModel() {

    // 기존 정보 있으면 가져오기(참조 가져오는 게 아닌 값 복사!!)
    private var _name : String? = AppUser.info?.name // 이름
    private var _gender : Int? = AppUser.info?.gender // 성별
    private var _birth : Calendar? // 생년월일
        = AppUser.info?.birth?.toDate()?.let { Calendar.getInstance().apply { time = it } }
    private var _diseaseSet : MutableSet<Disease>? =
        AppUser.info?.disease?.toMutableSet() // 보유 질환 set
    private var _allergenSet : MutableSet<Allergen>? =
        AppUser.info?.allergens?.toMutableSet() // 보유 알레르기 set

    // 읽기 전용 변수
    val name : String? get() = _name
    val gender : Int? get() = _gender
    val birth : Calendar? get() = _birth
    val diseaseSet : MutableSet<Disease>? get() = _diseaseSet
    val allergenSet : MutableSet<Allergen>? get() = _allergenSet

    // 이름 유효성 sealed class 및 변수 정의
    sealed class NameValidation { // 이름 유효성 검사 결과용 sealed class
        data object Empty : NameValidation()
        data object Duplicate : NameValidation()
        data object Valid : NameValidation()
    }
    private val _nameValidationResult = MutableLiveData<NameValidation>() // 이름 유효성 검사 결과
    val nameValidationResult: LiveData<NameValidation> = _nameValidationResult
    private val _nameValidationMessage = MutableLiveData<String>() // 이름 유효성 검사 메세지
    val nameValidationMessage: LiveData<String> = _nameValidationMessage


    fun contains(disease: Disease): Boolean {
        return diseaseSet?.contains(disease) == true
    }

    fun contains(allergen: Allergen): Boolean {
        return _allergenSet?.contains(allergen) == true
    }

    fun addToAllergenSet(allergen: Allergen) {
        _allergenSet?.add(allergen) ?:run { _allergenSet = mutableSetOf(allergen) }
    }

    fun removeFromAllergenSet(allergen: Allergen) {
        _allergenSet?.remove(allergen)
    }

    fun clearAllergenSet() {
        _allergenSet?.clear()
    }

    fun addToDiseaseSet(disease: Disease) {
        _diseaseSet?.add(disease) ?:run { _diseaseSet = mutableSetOf(disease) }
    }

    fun removeFromDiseaseSet(disease: Disease) {
        _diseaseSet?.remove(disease)
    }

    fun clearDiseaseSet() {
        _diseaseSet?.clear()
    }

    fun setBirth(year: Int, month:Int, day:Int) {
        _birth = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar의 month는 0부터 시작
            set(Calendar.DAY_OF_MONTH, day)
        }
    }

    fun setGender(gender: Int) {
        _gender = gender
    }

    fun setName(name: String) {
        _name = name
    }

    fun validateName(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _nameValidationResult.value = NameValidation.Empty
                _nameValidationMessage.value = "닉네임을 입력해주세요."
            } else if (name == AppUser.info?.name) { // 수정 시 사용자의 원래 이름에서 변경 사항 없을 때
                _nameValidationResult.value = NameValidation.Valid
                _nameValidationMessage.value = "현재 사용중인 닉네임입니다."
            } else {
                val isDuplicate = FirebaseHelper.checkDuplicateName(name)
                if (isDuplicate) {
                    _nameValidationResult.value = NameValidation.Duplicate
                    _nameValidationMessage.value = "중복된 이름입니다. 다시 설정해주세요!"
                } else {
                    _nameValidationResult.value = NameValidation.Valid
                    _nameValidationMessage.value = "사용 가능한 닉네임입니다."
                }
            }
        }
    }

    fun updateUserInfo() {
        val updatedInfo = HashMap<String, Any?>()
        _name?.let { name ->
            AppUser.info?.name = name
            updatedInfo["userName"] = name
        }
        _gender?.let { gender ->
            AppUser.info?.gender = gender
            updatedInfo["userGender"] = gender
        }
        _birth?.let { birth ->
            val birthTimeStamp = Timestamp(birth.time)
            AppUser.info?.birth = birthTimeStamp
            updatedInfo["userBirth"] = birthTimeStamp
        }
        _diseaseSet?.let { diseaseSet ->
            if (diseaseSet.isNotEmpty()) { // 질환 정보 전달 - enum명으로 DB 저장
                AppUser.info?.disease = diseaseSet
                updatedInfo["userDisease"] = diseaseSet.map{ it.name }
            }else{
                AppUser.info?.disease = null
                updatedInfo["userDisease"] = null
            }
        }
        _allergenSet?.let { allergenSet ->
            if (allergenSet.isNotEmpty()) { // 알레르기 정보 전달 - enum명으로 DB 저장
                AppUser.info?.allergens = allergenSet
                updatedInfo["userAllergens"] = allergenSet.map{ it.name }
            }else{
                AppUser.info?.allergens = null
                updatedInfo["userAllergens"] = null
            }
        }
        AppUser.id?.let { uid ->
            viewModelScope.launch {
                FirebaseHelper.updateUserData(uid, updatedInfo)
            }
        }
    }

    fun registerUserInfo() {
        val birthTimeStamp = Timestamp(birth?.time ?: Calendar.getInstance().time)

        AppUser.info =
            UserInfo(_name ?: "", _gender ?: 0, birthTimeStamp,
                diseaseSet?.ifEmpty { null }, allergenSet?.ifEmpty { null })

        val userInfo = hashMapOf(
            "userId" to Firebase.auth.currentUser?.uid,
            "userName" to _name,
            "userGender" to _gender,
            "userBirth" to birthTimeStamp,
            "userProfile" to null,
            "userDisease" to diseaseSet?.map{ it.name }?.ifEmpty { null }, // 질환 정보 전달 - enum명으로 DB 저장
            "userAllergens" to allergenSet?.map {it.name }?.ifEmpty { null }, // 알레르기 정보 전달 - enum명으로 DB 저장
            "lastActivationDate" to FieldValue.serverTimestamp()
        )

        FirebaseHelper.sendData(userInfo, "userInfo")
    }
}
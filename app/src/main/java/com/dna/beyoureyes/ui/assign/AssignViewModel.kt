package com.dna.beyoureyes.ui.assign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dna.beyoureyes.AppUser
import com.dna.beyoureyes.model.Allergen
import com.dna.beyoureyes.model.Disease
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssignViewModel(val mode: AssignMode) : ViewModel() {
    private val _name = MutableLiveData<String?>() // 별명
    val name : LiveData<String?> = _name

    private val _gender = MutableLiveData<Int?>() // 성별
    val gender : LiveData<Int?> = _gender

    private val _birth = MutableLiveData<String?>() // 생년월일
    val birth : LiveData<String?> = _birth

    private val _disease = MutableLiveData<MutableSet<Disease>>() // 보유 질환 set
    val disease : LiveData<MutableSet<Disease>> = _disease

    private val _allergen = MutableLiveData<MutableSet<Allergen>>() // 보유 알레르기 set
    val allergen : LiveData<MutableSet<Allergen>> = _allergen

    fun getUpdatedUserInfo() : HashMap<String, Any?> {
        val updatedInfo = HashMap<String, Any?>()
        _name.value?.let { name ->
            AppUser.info?.name = name
            updatedInfo["userName"] = name
        }
        _gender.value?.let { gender ->
            AppUser.info?.gender = gender
            updatedInfo["userGender"] = gender
        }
        _birth.value?.let { birth ->
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val birthDate = dateFormat.parse(birth) ?: Date()
            val birthTimeStamp = Timestamp(birthDate)
            AppUser.info?.birth = birthTimeStamp
            updatedInfo["userBirth"] = birthTimeStamp
        }
        _disease.value?.let { diseaseSet ->
            if (diseaseSet.isNotEmpty()) { // 질환 정보 전달 - enum명으로 DB 저장
                AppUser.info?.disease = diseaseSet
                updatedInfo["userDisease"] = diseaseSet.map{ it.name }
            }
        }
        _allergen.value?.let { allergenSet ->
            if (allergenSet.isNotEmpty()) { // 알레르기 정보 전달 - enum명으로 DB 저장
                AppUser.info?.allergens = allergenSet
                updatedInfo["userAllergens"] = allergenSet.map{ it.name }
            }
        }
        return updatedInfo
    }
}
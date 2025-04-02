package com.dna.beyoureyes.data.model

import android.net.Uri
import android.util.Log
import com.dna.beyoureyes.data.api.response.UserInfoResponse
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter

// Primary 생성자 (AssignActivity에서 활용)
class UserInfo(
    var name: String,                  // 사용자 나이
    var gender: Int,               // 사용자 성별
    var birth: LocalDate,     // 사용자 생일
    var disease: MutableSet<Disease>?,   // 사용자 질병 정보(nullable - 해당사항 없을 수 있으므로)
    var allergens: MutableSet<Allergen>?   // 사용자 알레르기 정보(nullable - 해당사항 없을 수 있으므로
) {
    val profileImgUri : Uri? get() = _profileImgUri
    private var _profileImgUri : Uri? = null // 프로필 사진 uri 값은 일단 private으로

    // age 값은 직접 입력받지 않고 birth 값에 따라 자동 관리
    var age: Int = calculateAge()
        private set

    // birth가 변경될 때, 자동으로 age도 업데이트되도록 setter 설정
    private var birthDate: LocalDate = birth
        set(value) {
            field = value
            age = calculateAge()
        }

    // Secondary 생성자: Spring 응답 UserInfoResponse를 처리  (SplashActivity에서 활용)
    constructor(response: UserInfoResponse) : this(
        name = response.profile.nickname,
        gender = response.profile.gender,
        birth = try {
            LocalDate.parse(response.profile.birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid birth date format(UserInfoResponse): ${response.profile.birth}")
        }, // 생년월일 Stirng -> LocalDate 파싱 실패 시 예외를 던짐
        disease = response.disease.entries
            .filter { it.value }
            .mapNotNull { enumValueOf<Disease>(it.key.uppercase()) }
            .toMutableSet(),
        allergens = response.allergy.entries
            .filter { it.value }
            .mapNotNull { enumValueOf<Allergen>(it.key.uppercase()) }
            .toMutableSet()
    )

    private fun calculateAge(): Int {
        val currentDate: LocalDate = LocalDate.now()
        val period: Period = Period.between(this.birth, currentDate)
        return period.years
    }

    fun setProfileImgUri(uri:Uri) {
        _profileImgUri = uri
    }

    fun getDailyEnergyRequirement(): Int {
        return if (gender == Gender.WOMAN.ordinal) { // 여성
            when(age){
                in 15..29 -> 2000
                in 30..49 -> 1900
                in 50..64 -> 1700
                in 65..74 -> 1600
                in 75..Int.MAX_VALUE -> 1500
                else -> 2000
            }
        } else { // 남성
            when(age){
                in 15..18 -> 2700
                in 19..29 -> 2600
                in 30..49 -> 2500
                in 50..64 -> 2200
                in 65..74 -> 2000
                in 75..Int.MAX_VALUE -> 1900
                else -> 2700
            }
        }
    }

    fun findMatchingAllergy(foodAllergens: Set<Allergen>) : Set<Allergen>? {
        return allergens?.intersect(foodAllergens)?.ifEmpty { null }
    }

}
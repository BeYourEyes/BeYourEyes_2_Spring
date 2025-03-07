package com.dna.beyoureyes.data.model

import android.net.Uri
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter


// 유저 정보 관리에 필요한 성별 enum 상수
enum class Gender {
    WOMAN, MAN
}

// 기본 생성자 (assign에서 활용, 프로필 이미지 등록x)
class UserInfo(
    var name: String,                  // 사용자 나이
    var gender: Int,               // 사용자 성별
    birthString: String,     // 사용자 생일
    var disease: MutableSet<Disease>?,   // 사용자 질병 정보(nullable - 해당사항 없을 수 있으므로)
    var allergens: MutableSet<Allergen>?,   // 사용자 알레르기 정보(nullable - 해당사항 없을 수 있으므로
    var profileImgPath: String? = null // 프로필 사진 DB 저장 경로
) {
    val profileImgUri : Uri? get() = _profileImgUri
    private var _profileImgUri : Uri? = null // 프로필 사진 uri 값은 일단 private으로

    var birth: LocalDate? = try {
        LocalDate.parse(birthString, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            null
        }
        set(value) {
            field = value
            age = calculateAge() // 사용자 생일 갱신 시 age 자동 갱신하도록 설정
        }
    var age: Int = calculateAge() // age 값은 직접 입력받지 않고 birth 값에 따라 자동 관리
        private set

    private fun calculateAge(): Int {
        val currentDate: LocalDate = LocalDate.now()
        val period: Period = Period.between(this.birth, currentDate)
        return period.years
    }

    // firebase에서 데이터 불러올 때 활용하는 생성자(프로필 이미지 O 유효성 상관X)
    /*
    constructor(name:String, gender:Int, birth: Timestamp,
                disease:ArrayList<String>, allergy:ArrayList<String>, profile:String)
            :this(name, gender, birth, disease.toDiseaseSet(), allergy.toAllergenSet(), profile)

     */

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
        return allergens?.intersect(foodAllergens)
    }

    fun allergySetToMap(): Map<String, Boolean>? {
        return this.allergens?.associate { allergen ->
            allergen.name.lowercase() to true
        }
    }

    fun diseaseSetToMap(): Map<String, Boolean>? {
        return this.disease?.associate { d ->
            d.name.lowercase() to true
        }
    }

    companion object {

        private fun ArrayList<String>.toAllergenSet(): MutableSet<Allergen>? {
            return this.mapNotNull { algName ->
                try {
                    Allergen.valueOf(algName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toMutableSet().ifEmpty { null }
        }

        private fun ArrayList<String>.toDiseaseSet(): MutableSet<Disease>? {
            return this.mapNotNull { diseaseName ->
                try {
                    Disease.valueOf(diseaseName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toMutableSet().ifEmpty { null }
        }

        /*
        fun parseFirebaseDoc(document: QueryDocumentSnapshot) : UserInfo? {
            val name = document.data.get("userName") as? String
            val birth = document.data.get("userBirth") as? Timestamp
            val gender = document.data.get("userGender") as? Long
            val diseaseList = document.data.get("userDisease") as ArrayList<String>
            val allergicList = document.data.get("userAllergy") as ArrayList<String>
            val profile = document.data.get("userProfile") as? String
            name?.let {  name ->
                birth?.let { birth ->
                    gender?.let { gender ->
                        profile?.let { profile ->
                            val user = UserInfo(name, gender.toInt(), birth, profile)
                            if ( diseaseList.size > 0) user.disease = diseaseList.toMutableSet()
                            if (allergicList.size > 0) user.allergic = allergicList.toMutableSet()
                            return user
                        }
                    }
                }
            }
            return null
        }

         */
    }
}
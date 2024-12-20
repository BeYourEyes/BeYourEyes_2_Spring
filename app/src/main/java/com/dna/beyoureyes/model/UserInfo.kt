package com.dna.beyoureyes.model

import android.net.Uri
import android.util.Log
import com.dna.beyoureyes.NutrientDailyValues
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// 유저 정보 관리에 필요한 성별 enum 상수
enum class Gender {
    WOMAN, MAN
}

// 기본 생성자 - 각 속성 값을 직접 전달받음. diseas, allergic은 디폴트값으로 null 세팅.
class UserInfo (
    var name : String,                  // 사용자 나이
    var gender : Int,               // 사용자 성별
    var birth : Timestamp,     // 사용자 생일
    var disease : MutableSet<Disease>?,   // 사용자 질병 정보(nullable - 해당사항 없을 수 있으므로)
    var allergens : MutableSet<Allergen>?,   // 사용자 알레르기 정보(nullable - 해당사항 없을 수 있으므로
    var age : Int,
    var profileImgPath: String? = null // 프로필 사진 DB 저장 경로
) {
    val profileImgUri : Uri? get() = _profileImgUri
    private var _profileImgUri : Uri? = null // 프로필 사진 uri 값은 일단 private으로

    init {
        age = getAge(birth)
    }

    // assign에서 활용하는 생성자(프로필 이미지 등록x)
    constructor(name:String, gender:Int, birth: Timestamp,
                disease:MutableSet<Disease>?, allergens: MutableSet<Allergen>?)
            :this(name, gender, birth, disease, allergens, getAge(birth))

    // firebase에서 데이터 불러올 때 활용하는 생성자(프로필 이미지 O 유효성 상관X)
    constructor(name:String, gender:Int, birth: Timestamp,
                disease:ArrayList<String>, allergy:ArrayList<String>, profile:String)
            :this(name, gender, birth, disease.toDiseaseSet(), allergy.toAllergenSet(), getAge(birth), profile)

    /*
    constructor(name:String, gender:Int, birth: Timestamp, profile: String)
            :this(name, gender, birth, mutableSetOf(), mutableSetOf(), getAge(birth), profile)

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

        fun getAge(birth: Timestamp) : Int {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            calendar.time = birth.toDate()
            val birthYear = calendar.get(Calendar.YEAR)
            val birthMonth = calendar.get(Calendar.MONTH) + 1
            val birthDay = calendar.get(Calendar.DAY_OF_MONTH)

            // 나이 계산
            var age = currentYear - birthYear
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--
            }
            return age
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
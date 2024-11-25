package com.dna.beyoureyes

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User
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
    var birth : String,     // 사용자 생일
    var disease : MutableSet<String>,   // 사용자 질병 정보(nullable - 해당사항 없을 수 있으므로)
    var allergic : MutableSet<String>,   // 사용자 알레르기 정보(nullable - 해당사항 없을 수 있으므로
    var age : Int,
    var userProfile: String = ""
) {
    init {
        age = getAge(birth)
    }
    constructor(name:String, gender:Int, birth: String, disease:ArrayList<String>, allergy:ArrayList<String>, age:Int)
            :this(name, gender, birth, disease.toMutableSet(), allergy.toMutableSet(), age)
    constructor(name:String, gender:Int, birth: String, disease:ArrayList<String>, allergy:ArrayList<String>)
            :this(name, gender, birth, disease.toMutableSet(), allergy.toMutableSet(), getAge(birth))

    constructor(name:String, gender:Int, birth: String, disease:ArrayList<String>, allergy:ArrayList<String>, profile:String)
            :this(name, gender, birth, disease.toMutableSet(), allergy.toMutableSet(), getAge(birth), profile)

    constructor(name:String, gender:Int, birth: String, profile: String)
            :this(name, gender, birth, mutableSetOf(), mutableSetOf(), getAge(birth), profile)

    //  사용자 맞춤 권장량 정보를 제공하는 get 메소드
    fun getDailyValues() : NutrientDailyValues {
        return NutrientDailyValues(gender, age, disease.toTypedArray())
    }

    fun hasDisease() : Boolean {
        return disease.isNotEmpty()
    }

    fun hasAllergy() : Boolean {
        return allergic.isNotEmpty()
    }

    fun getNutrisToCare() : Array<String> {
        val list = mutableListOf<String>()
        disease.forEach {
            when (it) {
                "고지혈증" -> list.addAll(arrayOf("지방", "포화지방", "콜레스테롤"))
                "고혈압" -> list.addAll(arrayOf("나트륨", "포화지방", "콜레스테롤"))
                "당뇨" -> list.addAll(arrayOf("당류", "콜레스테롤"))
            }
        }
        val set = list.toSet() // 중복 없애기
        return set.toTypedArray()
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

    companion object {

        fun getAge(birth: String) : Int {
            // 날짜 형식 정의
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

            val birthDate = dateFormat.parse(birth)

            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            calendar.time = birthDate
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
        fun parseFirebaseDoc(document: QueryDocumentSnapshot) : UserInfo? {
            val name = document.data.get("userName") as? String
            val birth = document.data.get("userBirth") as? String
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
    }
}
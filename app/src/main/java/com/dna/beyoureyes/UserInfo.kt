package com.dna.beyoureyes

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User
import java.sql.Timestamp
import java.util.Calendar

// 유저 정보 관리에 필요한 성별 enum 상수
enum class Gender {
    WOMAN, MAN
}

// 기본 생성자 - 각 속성 값을 직접 전달받음. diseas, allergic은 디폴트값으로 null 세팅.
class UserInfo (
    var name : String,
    var gender : Int,
    var birth : Calendar, // birth의 datatype??
    var disease : MutableSet<String>,   // 사용자 질병 정보(nullable - 해당사항 없을 수 있으므로)
    var allergic : MutableSet<String>   // 사용자 알레르기 정보(nullable - 해당사항 없을 수 있으므로
) {
    var age = getAge()
    constructor(name:String, gender:Int, birth: Calendar, disease:Array<String>, allergic:Array<String>)
            :this(name, gender, birth, disease.toMutableSet(), allergic.toMutableSet())

    constructor(name:String, gender:Int, birth: Calendar)
            :this(name, gender, birth, mutableSetOf(), mutableSetOf())

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

    fun getAge(): Int{
        val currentDate = Calendar.getInstance()  // 현재 날짜를 Calendar로 가져옴
        val birthYear = birth.get(Calendar.YEAR)
        val birthMonth = birth.get(Calendar.MONTH)
        val birthDay = birth.get(Calendar.DAY_OF_MONTH)
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH)
        val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear

        // 현재 생일이 지났는지 확인
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--  // 생일이 지나지 않았다면 한 살 빼기
        }

        return age
    }

    fun getNutrisToCare() : Array<String> {
        var list = mutableListOf<String>()
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
    companion object {
        fun parseFirebaseDoc(document: QueryDocumentSnapshot) : UserInfo? {
            val name = document.data.get("userName") as? String
            val age = document.data.get("userAge") as? Long
            val gender = document.data.get("userSex") as? Long
            val birth = document.data.get("userBirth") as? Timestamp
            val diseaseList = document.data.get("userDisease") as ArrayList<String>
            val allergicList = document.data.get("userAllergic") as ArrayList<String>
            name?.let { name ->
                birth?.let { birth ->
                    gender?.let { gender->
                        /*
                        val user = UserInfo(name, gender.toInt(), birth)
                        if ( diseaseList.size > 0) user.disease = diseaseList.toMutableSet()
                        if (allergicList.size > 0) user.allergic = allergicList.toMutableSet()
                        return user
                         */
                    }
                }
            }
            return null
        }
    }
}
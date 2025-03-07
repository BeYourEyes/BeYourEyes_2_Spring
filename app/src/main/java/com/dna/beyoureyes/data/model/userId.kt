package com.dna.beyoureyes.data.model

import android.app.Application
import android.net.Uri
import android.util.Log
import com.dna.beyoureyes.data.api.response.UserData

// 무시
class userId : Application() {
    var userId: String? = null
}


// id만 저장하는 게 아니므로 객체명을 AppUser로 변경(이름 중복을 피하기 위해 앞에 App 붙임)
object AppUser {
    // 객체를 id, userInfo(기존 클래스) 속성을 갖도록 정의
    var id : String? = null
    var info : UserInfo? = null

    fun setInfo(springUserData: UserData) {
        Log.d("API_TEST", "test")
        val info = UserInfo(
            name = springUserData.userInfo.userNickname,
            gender = springUserData.userInfo.userGender,
            birthString = springUserData.userInfo.userBirth,
            disease = springUserData.disease.entries
                .filter{it.value}
                .mapNotNull { enumValueOf<Disease>(it.key.uppercase()) }.toMutableSet(),
            allergens = springUserData.allergy.entries
                .filter{it.value}
                .mapNotNull { enumValueOf<Allergen>(it.key.uppercase()) }.toMutableSet()
        )
        AppUser.info = info
    }

    fun setProfileImgUri(uri: Uri) {
        info?.setProfileImgUri(uri)
    }
}

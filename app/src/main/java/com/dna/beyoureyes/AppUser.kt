package com.dna.beyoureyes

import android.net.Uri
import android.util.Log
import com.dna.beyoureyes.data.api.response.UserData
import com.dna.beyoureyes.data.model.Allergen
import com.dna.beyoureyes.data.model.Disease
import com.dna.beyoureyes.data.model.UserInfo


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

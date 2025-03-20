package com.dna.beyoureyes

import android.net.Uri
import com.dna.beyoureyes.data.api.response.UserInfoResponse
import com.dna.beyoureyes.data.model.Allergen
import com.dna.beyoureyes.data.model.Disease
import com.dna.beyoureyes.data.model.UserInfo
import org.threeten.bp.LocalDate


object AppUser {
    // id와 info를 private set으로 변경하여 직접 수정 불가능하게 만듦(데이터 일관성 개선)
    var id: String? = null
        private set

    var info: UserInfo? = null
        private set

    // UserInfoResponse를 직접 UserInfo로 변환하여 생성(Secondary 생성자 활용)
    fun setInfo(springUserData: UserInfoResponse) {
        info = UserInfo(springUserData)
    }

    // 속성별 값을 직접 입력받아 UserInfo 생성(Primary 생성자 활용)
    fun setInfo(name: String, gender: Int, birth: LocalDate,
                disease: MutableSet<Disease>?, allergens: MutableSet<Allergen>?) {
        info = UserInfo(name, gender, birth, disease, allergens)
    }

    // ID 설정을 별도의 메서드로 제공하여 일관성 유지
    fun setId(userId: String) {
        id = userId
    }

    // 프로필 이미지 URI 설정도 UserInfo 내부에서만 관리
    fun setProfileImgUri(uri: Uri) {
        info?.setProfileImgUri(uri)
    }

}

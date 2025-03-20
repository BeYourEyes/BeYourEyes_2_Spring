package com.dna.beyoureyes.data.api.response

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("userInfo") val profile: BasicProfile,
    val allergy: Map<String, Boolean>,
    val disease: Map<String, Boolean>
) {
    data class BasicProfile(
        @SerializedName("user_birth") val birth: String,
        @SerializedName("user_gender") val gender: Int,
        @SerializedName("user_nickname") val nickname: String
    )
}
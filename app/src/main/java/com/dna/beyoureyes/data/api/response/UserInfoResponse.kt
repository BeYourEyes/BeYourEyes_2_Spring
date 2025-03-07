package com.dna.beyoureyes.data.api.response

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    val status: String,
    val message: String,
    val data: UserData
)

data class UserData(
    @SerializedName("userInfo") val userInfo: BasicProfile,
    val allergy: Map<String, Boolean>,
    val disease: Map<String, Boolean>
)

data class BasicProfile(
    @SerializedName("user_birth") val userBirth: String,
    @SerializedName("user_gender") val userGender: Int,
    @SerializedName("user_nickname") val userNickname: String
)
package com.dna.beyoureyes.data.api.request

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @SerializedName("user_birth") val userBirth: String,
    @SerializedName("user_gender") val userGender: Int,
    @SerializedName("user_nickname") val userNickname: String
)
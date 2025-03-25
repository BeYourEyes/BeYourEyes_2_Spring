package com.dna.beyoureyes.data.api.request

import com.google.gson.annotations.SerializedName

data class JoinRequest(
    //@SerializedName("fcm_token") val fcmToken: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("user_birth") val userBirth: String,
    @SerializedName("user_gender") val userGender: Int,
    @SerializedName("user_nickname") val userNickname: String,
    val allergy: Map<String, Boolean>,
    val disease: Map<String, Boolean>
)

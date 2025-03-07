package com.dna.beyoureyes.data.api.request

data class JoinRequest(
    val device_id: String,
    val user_birth: String,
    val user_gender: Int,
    val user_nickname: String,
    val allergy: Map<String, Boolean>,
    val disease: Map<String, Boolean>
)

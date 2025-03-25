package com.dna.beyoureyes.data.api.response

import com.google.gson.annotations.SerializedName

data class FoodImageResponse(
    @SerializedName("img_url") val imgUrl: String,
    val datetime: String
)

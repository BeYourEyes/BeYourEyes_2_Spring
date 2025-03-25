package com.dna.beyoureyes.data.api.request

import com.google.gson.annotations.SerializedName

data class DeviceIdRequest(
    @SerializedName("device_id") val deviceId: String
)

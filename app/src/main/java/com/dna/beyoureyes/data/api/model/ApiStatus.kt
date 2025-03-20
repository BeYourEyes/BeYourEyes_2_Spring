package com.dna.beyoureyes.data.api.model

enum class ApiStatus(val value: String) {
    SUCCESS("SUCCESS"),
    FAIL("FAIL"),
    SERVER_ERROR("ERROR"),
    NETWORK_ERROR("NETWORK_ERROR"),
    UNKNOWN("UNKNOWN");

    override fun toString(): String {
        return value
    }

    companion object {
        fun fromString(status: String?): ApiStatus {
            return values().find { it.value == status } ?: UNKNOWN
        }
    }
}
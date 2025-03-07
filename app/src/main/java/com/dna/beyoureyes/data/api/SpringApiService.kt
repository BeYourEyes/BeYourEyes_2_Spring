package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.request.DeviceIdRequest
import com.dna.beyoureyes.data.api.request.JoinRequest
import com.dna.beyoureyes.data.api.response.SpringApiResponse
import com.dna.beyoureyes.data.api.response.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApi {
    @POST("/user/login")
    suspend fun login(@Body request: DeviceIdRequest): Response<SpringApiResponse>

}

interface JoinApi {
    @POST("/user/save-user")
    suspend fun join(@Body request: JoinRequest): Response<SpringApiResponse>
}

interface UserInfoApi {
    @GET("/user/user-info")
    suspend fun getUserInfo(): Response<UserInfoResponse>
}

interface CheckNicknameApi {
    @GET("/user/check-nickname")
    suspend fun checkNickname(): Response<SpringApiResponse>
}
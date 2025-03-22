package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.request.DeviceIdRequest
import com.dna.beyoureyes.data.api.request.JoinRequest
import com.dna.beyoureyes.data.api.response.SpringApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 인증이 필요 없는 Spring API 서비스 Interface
 */

interface NoAuthSpringApiService {

    @POST(USER_LOGIN) // 로그인(기기ID를 바탕으로 기존 사용자인지 검사)
    suspend fun login(@Body request: DeviceIdRequest): Response<SpringApiResponse<String>>

    @POST(USER_JOIN) // 회원가입(새 사용자 등록)
    suspend fun join(@Body request: JoinRequest): Response<SpringApiResponse<String>>

    @GET(CHECK_NICKNAME) // 사용자 별명 중복 조회
    suspend fun checkNickname(@Query("nickname") nickname: String): Response<SpringApiResponse<Boolean>>

    @POST(REFRESH_TOKEN) // 액세스 토큰 재발급
    suspend fun refreshToken(@Header("Authorization") expiredToken: String): Response<SpringApiResponse<String>>

    companion object {
        //  API 변경 시 유지보수 목적으로 경로를 companion object로 따로 관리
        private const val USER_LOGIN = "/v2/user/login"
        private const val USER_JOIN = "/v2/user/save-user"
        private const val CHECK_NICKNAME = "/v2/user/check-nickname"
        private const val REFRESH_TOKEN = "/v2/user/refresh-token"
    }
}
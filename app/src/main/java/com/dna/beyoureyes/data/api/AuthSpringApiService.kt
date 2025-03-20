package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.model.NutritionInfo
import com.dna.beyoureyes.data.api.request.FoodRecordRequest
import com.dna.beyoureyes.data.api.request.ProfileRequest
import com.dna.beyoureyes.data.api.response.FoodHistoryResponse
import com.dna.beyoureyes.data.api.response.SpringApiResponse
import com.dna.beyoureyes.data.api.response.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

/**
 * 토큰 인증이 필요한 Spring API 서비스 Interface
 */

interface AuthSpringApiService {

    @GET(USER_INFO) // 사용자 정보 조회
    suspend fun getUserInfo(): Response<SpringApiResponse<UserInfoResponse>>

    @GET(FOOD_TODAY) // 오늘 섭취한 모든 기록
    suspend fun getTodayFoodHistories(): Response<SpringApiResponse<List<FoodHistoryResponse>>>

    @GET(FOOD_TODAY_SUM) // 오늘 섭취한 영양소, 칼로리 값 합계
    suspend fun getTodayIntakeSum(): Response<SpringApiResponse<NutritionInfo>>

    @PATCH(UPDATE_PROFILE) // 사용자 기본 정보(이름, 성별, 생년월일) 업데이트
    suspend fun updateProfile(@Body request: ProfileRequest): Response<SpringApiResponse<Boolean>>

    @PATCH(UPDATE_DISEASE) // 사용자 질환 정보 업데이트
    suspend fun updateDisease(@Body diseaseMap: Map<String, Boolean>): Response<SpringApiResponse<Boolean>>

    @PATCH(UPDATE_ALLERGY) // 사용자 알레르기 정보 업데이트
    suspend fun updateAllergens(@Body allergyMap: Map<String, Boolean>): Response<SpringApiResponse<Boolean>>

    @POST(RECORD_FOOD) // 섭취 기록 저장
    suspend fun recordFood(@Body request: FoodRecordRequest): Response<SpringApiResponse<Boolean>>

    @DELETE(DELETE_USER)
    suspend fun deleteUser(): Response<SpringApiResponse<Boolean>>

    companion object {
        //  API 변경 시 유지보수 목적으로 경로를 companion object로 따로 관리
        private const val USER_INFO = "/user/user-info"
        private const val FOOD_TODAY = "/food/today"
        private const val FOOD_TODAY_SUM = "/food/today/summary"
        private const val UPDATE_PROFILE = "/user/update"
        private const val UPDATE_DISEASE = "/update/disease"
        private const val UPDATE_ALLERGY = "/update/allergy"
        private const val RECORD_FOOD = "/food/record"
        private const val DELETE_USER = "/user/delete"
    }
}
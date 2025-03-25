package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.model.NutritionInfo
import com.dna.beyoureyes.data.api.request.ProfileRequest
import com.dna.beyoureyes.data.api.response.FoodHistoryResponse
import com.dna.beyoureyes.data.api.response.FoodImageResponse
import com.dna.beyoureyes.data.api.response.SpringApiResponse
import com.dna.beyoureyes.data.api.response.UserInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST(RECORD_FOOD) // 섭취 기록 저장
    suspend fun recordFood(
        @Part image: MultipartBody.Part,
        @Part("food_data") foodData: RequestBody  // 바로 NutritionInfo를 JSON으로 넘겨주는 부분
    ): Response<SpringApiResponse<FoodImageResponse>>

    @DELETE(DELETE_USER)
    suspend fun deleteUser(): Response<SpringApiResponse<Boolean>>

    companion object {
        //  API 변경 시 유지보수 목적으로 경로를 companion object로 따로 관리
        private const val USER_INFO = "/v2/user/user-info"
        private const val FOOD_TODAY = "/v2/food/today"
        private const val FOOD_TODAY_SUM = "/v2/food/today/summary"
        private const val UPDATE_PROFILE = "/v2/user/update"
        private const val UPDATE_DISEASE = "/v2/update/disease"
        private const val UPDATE_ALLERGY = "/v2/update/allergy"
        private const val RECORD_FOOD = "/v2/food/record"
        private const val DELETE_USER = "/v2/user/delete"
    }
}
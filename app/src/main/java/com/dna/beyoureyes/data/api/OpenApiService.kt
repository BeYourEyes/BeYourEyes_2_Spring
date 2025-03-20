package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.response.OpenApiFoodResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 전국통합식품영양성분정보 공공 API 서비스 Interface
 */

interface OpenApiService {
    @GET("tn_pubr_public_nutri_process_info_api")
    suspend fun getFood (
        @Query("serviceKey") apiKey: String = ApiConfig.FOOD_OPEN_API_KEY,  // 서비스 키 전달
        @Query("pageNo") pageNo: String,
        @Query("numOfRows") numOfRows: String,
        @Query("type") type: String,
        @Query("itemMnftrRptNo") itemMnftrRptNp: String
    ): OpenApiFoodResponse
}
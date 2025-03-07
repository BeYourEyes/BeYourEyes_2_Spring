package com.dna.beyoureyes.data.api

import com.dna.beyoureyes.data.api.response.FoodOpenApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodOpenApiService {
    @GET("tn_pubr_public_nutri_process_info_api?serviceKey=${APIKey.API_KEY}")
    suspend fun getFood (
        @Query("serviceKey") apiKey: String = APIKey.API_KEY,
        @Query("pageNo") pageNo: String,
        @Query("numOfRows") numOfRows: String,
        @Query("type") type: String,
        @Query("itemMnftrRptNo") itemMnftrRptNp: String
    ): FoodOpenApiResponse
}
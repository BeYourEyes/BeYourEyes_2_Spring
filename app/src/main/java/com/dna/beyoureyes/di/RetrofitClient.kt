package com.dna.beyoureyes.di

import com.dna.beyoureyes.data.api.FoodOpenApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://api.data.go.kr/openapi/"

    val apiService: FoodOpenApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodOpenApiService::class.java)
    }
}
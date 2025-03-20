package com.dna.beyoureyes.di

import com.dna.beyoureyes.data.api.OpenApiService
import retrofit2.Retrofit

object OpenApiClient {
    private const val BASE_URL = "http://api.data.go.kr/openapi/"

    private val retrofit: Retrofit by lazy {
        NetworkModule.createRetrofit(BASE_URL, NetworkModule.noAuthClient)
    }

    val apiService: OpenApiService by lazy {
        retrofit.create(OpenApiService::class.java)
    }
}
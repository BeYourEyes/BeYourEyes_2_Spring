package com.dna.beyoureyes.di

import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 공통 네트워크 설정 객체
 *
 * 인증이 필요없는 클라이언트(공공 API와 springAPI 중 일부에서 활용)와
 * 인증이 필요한 클라이언트(springAPI 일부) 호출 정의
 * 공통적인 GsonConverterFactory, HttpLoggingInterceptor 정의도 포함
 */

object NetworkModule {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 인증이 필요 없는 OkHttpClient (공공 API용)
    val noAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // 인증이 필요한 OkHttpClient (Spring API용) - AuthInterceptor 필요
    fun getAuthClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Retrofit 객체 생성 함수
    fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
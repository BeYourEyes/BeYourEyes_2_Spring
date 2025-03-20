package com.dna.beyoureyes.di

import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpringClient {
    private const val BASE_URL = "http://3.39.223.116:8080"

    // AuthInterceptor가 없는 OkHttpClient
    private val noAuthClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    // AuthInterceptor가 있는 OkHttpClient
    fun getAuthClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // AuthInterceptor가 없는 Retrofit
    private val noAuthRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(noAuthClient)
            .build()
    }

    // AuthInterceptor가 있는 Retrofit
    fun getAuthRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getAuthClient(authInterceptor))
            .build()
    }
}
package com.dna.beyoureyes.di

import com.dna.beyoureyes.BuildConfig
import com.dna.beyoureyes.data.api.AuthSpringApiService
import com.dna.beyoureyes.data.api.NoAuthSpringApiService
import com.dna.beyoureyes.data.api.interceptor.AuthInterceptor
import retrofit2.Retrofit

object SpringClient {
    private const val BASE_URL = BuildConfig.SPRING_BASE_URL

    //////////////////////////////////////////////////////////////////////////////////////////
    // 인증이 필요 없는 Retrofit 인스턴스
    private val noAuthRetrofit: Retrofit by lazy {
        NetworkModule.createRetrofit(BASE_URL, NetworkModule.noAuthClient)
    }
    // 인증이 필요 없는 API 서비스 (로그인, 회원가입)
    val noAuthSpringApi: NoAuthSpringApiService by lazy {
        noAuthRetrofit.create(NoAuthSpringApiService::class.java)
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // 인증이 필요한 Retrofit 인스턴스 (초기에는 null, 설정 후 사용)
    private var authRetrofit: Retrofit? = null

    // authRetrofit의 인증 설정
    fun initAuthClient(authInterceptor: AuthInterceptor) {
        authRetrofit = NetworkModule.createRetrofit(
            BASE_URL,
            NetworkModule.getAuthClient(authInterceptor)
        )
    }

    // 인증이 필요한 API 서비스 (유저 정보 조회 등)
    val authSpringApi: AuthSpringApiService
        get() {
            requireNotNull(authRetrofit) {
                "AuthInterceptor가 설정되지 않았습니다. initAuthClient()를 먼저 호출하세요."
            }
            return authRetrofit!!.create(AuthSpringApiService::class.java)
        }

}
package com.dna.beyoureyes.data.api.interceptor

import com.dna.beyoureyes.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authRepository: AuthRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        // 액세스 토큰을 헤더에 추가
        val accessToken = runBlocking { authRepository.getAccessToken().first() }
        if (!accessToken.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(request.build())

        // 401 에러 처리 (토큰 만료)
        if (response.code == 401) {
            // 토큰 삭제 및 로그아웃 처리
            runBlocking { authRepository.deleteToken() }
            // 로그아웃 처리 로직 추가 (예: 로그인 화면으로 이동)
        }

        return response
    }
}
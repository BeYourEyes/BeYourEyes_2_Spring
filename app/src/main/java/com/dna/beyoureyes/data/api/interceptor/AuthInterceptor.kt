package com.dna.beyoureyes.data.api.interceptor

import com.dna.beyoureyes.data.api.model.AuthException
import com.dna.beyoureyes.data.repository.AuthRepository
import com.dna.beyoureyes.di.SpringClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authRepository: AuthRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        // 현재 저장된 액세스 토큰을 헤더에 추가
        val accessToken = runBlocking { authRepository.getAccessToken().first() }
        if (!accessToken.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $accessToken")
        }

        var response = chain.proceed(request.build())

        // 401 Unauthorized 응답이 왔을 경우 -> 토큰이 만료된 상태
        if (response.code == 401) {
            synchronized(this) { // 여러 요청이 동시에 갱신되지 않도록 동기화
                response.close() // 기존 응답 닫기

                // 새로운 토큰을 가져오기 위해 refreshToken() 호출
                val newAccessToken = runBlocking { refreshToken(accessToken!!) }

                if (!newAccessToken.isNullOrEmpty()) {
                    // 새로운 토큰을 사용하여 요청을 다시 수행
                    request.removeHeader("Authorization") // 기존 토큰 제거
                    request.addHeader("Authorization", "Bearer $newAccessToken") // 새 토큰 추가
                    response = chain.proceed(request.build())
                } else {
                    // 토큰 갱신 실패 → 예외 처리
                    runBlocking { authRepository.deleteToken() }
                    throw AuthException("Failed To Refresh Acees Token!")
                }
            }
        }

        return response
    }

    // 토큰 재발급을 위한 API 호출
    private suspend fun refreshToken(expiredToken: String): String? {
        return try {
            val response = SpringClient.noAuthSpringApi.refreshToken("Bearer $expiredToken") // API 호출
            if (response.isSuccessful) {
                val newToken = response.body()?.data
                newToken?.let { authRepository.saveToken(it) } // 새로운 토큰 저장
                newToken
            } else {
                null // 실패 시 null 반환
            }
        } catch (e: Exception) {
            null
        }
    }
}
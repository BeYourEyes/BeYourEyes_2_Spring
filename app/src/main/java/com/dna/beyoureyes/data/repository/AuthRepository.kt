package com.dna.beyoureyes.data.repository

import com.dna.beyoureyes.data.local.TokenManager
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun getAccessToken(): Flow<String?>
    suspend fun saveToken(accessToken: String)
    suspend fun deleteToken()
}

class AuthRepositoryImpl(private val tokenManager: TokenManager) : AuthRepository {
    override suspend fun getAccessToken(): Flow<String?> {
        return tokenManager.accessToken
    }

    override suspend fun saveToken(accessToken: String) {
        tokenManager.saveToken(accessToken)
    }

    override suspend fun deleteToken() {
        tokenManager.deleteToken()
    }
}
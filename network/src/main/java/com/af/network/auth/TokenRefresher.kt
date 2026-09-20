package com.af.network.auth

interface TokenRefresher {
    suspend fun refreshToken(): Boolean
}

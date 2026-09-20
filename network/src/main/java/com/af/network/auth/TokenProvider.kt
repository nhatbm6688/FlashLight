package com.af.network.auth

interface TokenProvider {
    fun getAccessToken(): String?
}

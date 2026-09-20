package com.af.network.di

data class NetworkConfig(
    val baseUrl: String,
    val enableLogging: Boolean,
    val connectTimeoutSeconds: Long = 30L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val noAuthPathSegments: List<String> = emptyList()
)

package com.af.network.auth

import com.af.network.di.NetworkConfig
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRefresher: Lazy<TokenRefresher>,
    private val tokenProvider: TokenProvider,
    private val config: NetworkConfig
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (config.noAuthPathSegments.any { path.contains(it) }) return null

        val refreshed = runBlocking {
            try {
                tokenRefresher.get().refreshToken()
            } catch (e: Exception) {
                false
            }
        }
        if (!refreshed) return null

        val newToken = tokenProvider.getAccessToken()?.takeIf { it.isNotEmpty() } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}

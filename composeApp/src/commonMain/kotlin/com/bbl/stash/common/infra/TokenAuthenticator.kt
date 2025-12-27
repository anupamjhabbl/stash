package com.bbl.stash.common.infra

import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.Constants
import io.ktor.client.plugins.auth.providers.BearerTokens

class TokenAuthenticator(
    val authPreferencesUseCase: AuthPreferencesUseCase,
    val userAuthUseCase: UserAuthUseCase
) {
    suspend fun refresh(): BearerTokens? {
        val refreshToken = authPreferencesUseCase.getRefreshToken()
            ?: return null

        return try {
            val response = userAuthUseCase
                .getNewAccessToken("${Constants.HTTPHeaders.AUTHORIZATION_BEARER} $refreshToken")

            if (response.isSuccess && response.data != null) {
                val newAccessToken = response.data.accessToken

                authPreferencesUseCase.saveAccessToken(newAccessToken)

                BearerTokens(
                    accessToken = newAccessToken,
                    refreshToken = refreshToken
                )
            } else {
                null
            }
        } catch (e: Exception) {
            when (e) {
                is io.ktor.client.plugins.ClientRequestException -> {
                    if (e.response.status.value == 401 ||
                        e.response.status.value == 403
                    ) {
                        clearTokens()
                    }
                }
            }
            null
        }
    }

    fun getAccessToken(): String {
        return authPreferencesUseCase.getAccessToken()
    }

    fun getRefreshToken(): String? {
        return authPreferencesUseCase.getRefreshToken()
    }

    private fun clearTokens() {
        authPreferencesUseCase.removeAccessToken()
        authPreferencesUseCase.removeRefreshToken()
    }
}
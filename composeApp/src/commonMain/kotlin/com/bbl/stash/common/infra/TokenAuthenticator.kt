package com.bbl.stash.common.infra

import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.Constants
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TokenAuthenticator(
    val authPreferencesUseCase: AuthPreferencesUseCase,
    val userAuthUseCase: UserAuthUseCase
) {
    private val mutex = Mutex()
    suspend fun refresh(): BearerTokens? {
        return mutex.withLock {
            val refreshToken = authPreferencesUseCase.getRefreshToken()
                ?: return@withLock null

            try {
                val accessToken = authPreferencesUseCase.getAccessToken()
                if (!isExpired(accessToken)) {
                    return@withLock BearerTokens(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                }
                val response = userAuthUseCase
                    .getNewAccessToken("${Constants.HTTPHeaders.AUTHORIZATION_BEARER} $refreshToken")

                if (response.isSuccess && response.data != null) {
                    val newAccessToken = response.data.accessToken

                    authPreferencesUseCase.saveAccessToken(newAccessToken)
                    authPreferencesUseCase.saveRefreshToken(refreshToken)

                    return@withLock BearerTokens(
                        accessToken = newAccessToken,
                        refreshToken = refreshToken
                    )
                } else {
                    return@withLock null
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
                return@withLock null
            }
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

    fun getCurrentTokens(): BearerTokens? {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()

        return if (refreshToken != null) {
            BearerTokens(accessToken, refreshToken)
        } else {
            null
        }
    }

    @OptIn(ExperimentalTime::class)
    fun isExpired(token: String,  skewSeconds: Long = 30): Boolean {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return true

            val json = payload
                .decodeBase64Bytes()
                .decodeToString()

            val exp = Json.parseToJsonElement(json)
                .jsonObject["exp"]
                ?.jsonPrimitive
                ?.long
                ?: return true

            val nowSeconds =
                Clock.System.now().toEpochMilliseconds() / 1000

            nowSeconds >= (exp - skewSeconds)
        } catch (_: Exception) {
            true
        }
    }
}
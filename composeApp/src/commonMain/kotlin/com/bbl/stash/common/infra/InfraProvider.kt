package com.bbl.stash.common.infra

import com.bbl.stash.common.Constants
import com.bbl.stash.common.PlatformConstants
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object InfraProvider {
    private const val SERP_BASE_API =  "https://serpapi.com/"
    private const val DEVICE_ID = "a3f1c9e2-8b4a-4d1a-9e55-123abc456"

    fun getHttpClient(
        engine: HttpClientEngine,
        tokenAuthenticator: TokenAuthenticator
    ): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header(Constants.HTTPHeaders.X_DEVICE_ID, DEVICE_ID)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = tokenAuthenticator.getAccessToken()
                        val refreshToken = tokenAuthenticator.getRefreshToken()

                        if (refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }

                    refreshTokens { tokenAuthenticator.refresh() }

                    sendWithoutRequest { request ->
                        request.headers[Constants.HTTPHeaders.AUTHORIZATION] == null
                    }
                }
            }
        }
    }

    fun getHttpClient(
        engine: HttpClientEngine
    ) : HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header(Constants.HTTPHeaders.X_DEVICE_ID, DEVICE_ID)
            }
        }
    }


    fun getKtorFitInstance(tokenAuthenticator: TokenAuthenticator): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(PlatformConstants.getBaseUrl())
            .httpClient(getHttpClient(PlatformInfraProvider.getHttpClientEngine(), tokenAuthenticator))
            .build()
    }

    fun getKtorFitInstance(): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(PlatformConstants.getBaseUrl())
            .httpClient(getHttpClient(PlatformInfraProvider.getHttpClientEngine()))
            .build()
    }

    fun getKtorFitInstanceForSerp(): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(SERP_BASE_API)
            .httpClient(getHttpClient(PlatformInfraProvider.getHttpClientEngine()))
            .build()
    }

}

expect object PlatformInfraProvider {
    fun getHttpClientEngine(): HttpClientEngine
}
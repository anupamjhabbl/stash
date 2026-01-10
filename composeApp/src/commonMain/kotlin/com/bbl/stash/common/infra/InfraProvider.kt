package com.bbl.stash.common.infra

import com.bbl.stash.common.Constants
import com.bbl.stash.common.DeviceIdProvider
import com.bbl.stash.common.PlatformConstants
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
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
    const val AUTH = "WithAuthenticator"
    const val NO_AUTH = "WithoutAuthenticator"

    fun getHttpClient(
        engine: HttpClientEngine,
        tokenAuthenticator: TokenAuthenticator,
        deviceIdProvider: DeviceIdProvider
    ): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header(Constants.HTTPHeaders.X_DEVICE_ID, deviceIdProvider.get())
            }
            install(Auth) {
                bearer {
                    loadTokens { tokenAuthenticator.getCurrentTokens() }

                    refreshTokens { tokenAuthenticator.refresh() }

                    sendWithoutRequest { request ->
                        request.headers[Constants.HTTPHeaders.AUTHORIZATION] == null
                    }
                }
            }
        }
    }

    fun getHttpClient(
        engine: HttpClientEngine,
        deviceIdProvider: DeviceIdProvider
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
                header(Constants.HTTPHeaders.X_DEVICE_ID, deviceIdProvider.get())
            }
        }
    }

    fun getKtorFitInstance(httpClient: HttpClient): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(PlatformConstants.getBaseUrl())
            .httpClient(httpClient)
            .build()
    }

    fun getKtorFitInstanceForSerp(httpClient: HttpClient): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(SERP_BASE_API)
            .httpClient(httpClient)
            .build()
    }

}

expect object PlatformInfraProvider {
    fun getHttpClientEngine(): HttpClientEngine
}
package com.example.stash.di

import com.example.stash.common.PlatformConstants
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object InfraProvider {
    fun getHttpClient(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }

    fun getKtorFitInstance(): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(PlatformConstants.getBaseUrl())
            .httpClient(getHttpClient(PlatformInfraProvider.getHttpClientEngine()))
            .build()
    }
}

expect object PlatformInfraProvider {
    fun getHttpClientEngine(): HttpClientEngine
}
package com.example.stash.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual object PlatformInfraProvider {
    actual fun getHttpClientEngine(): HttpClientEngine {
        return OkHttp.create()
    }
}
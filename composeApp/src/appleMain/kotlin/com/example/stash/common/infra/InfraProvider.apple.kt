package com.example.stash.common.infra

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual object PlatformInfraProvider {
    actual fun getHttpClientEngine(): HttpClientEngine {
        return Darwin.create()
    }
}
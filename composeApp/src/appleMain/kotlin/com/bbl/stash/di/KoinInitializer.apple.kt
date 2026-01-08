package com.bbl.stash.di

import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(databaseModule, preferenceManagerModule, commonModule, viewModelModule, iosModule)
        }
    }
}
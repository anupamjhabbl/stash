package com.example.stash.di

import com.example.stash.common.infra.AppleSecureStorage
import com.example.stash.common.infra.SecurePreferenceManager
import com.example.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { SecurePreferenceManager() }
    single<SecureStorage> { AppleSecureStorage(get<SecurePreferenceManager>()) }
}
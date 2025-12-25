package com.example.stash.di

import com.example.stash.common.infra.AndroidSecureStorage
import com.example.stash.common.infra.EncryptedPreferenceManager
import com.example.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { EncryptedPreferenceManager(get()) }
    single<SecureStorage> { AndroidSecureStorage(get<EncryptedPreferenceManager>()) }
}
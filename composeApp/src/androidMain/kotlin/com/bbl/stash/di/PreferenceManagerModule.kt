package com.bbl.stash.di

import com.bbl.stash.common.infra.AndroidSecureStorage
import com.bbl.stash.common.infra.EncryptedPreferenceManager
import com.bbl.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { EncryptedPreferenceManager(get()) }
    single<SecureStorage> { AndroidSecureStorage(get<EncryptedPreferenceManager>()) }
}
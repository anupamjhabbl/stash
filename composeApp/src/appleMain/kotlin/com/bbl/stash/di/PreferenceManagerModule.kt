package com.bbl.stash.di

import com.bbl.stash.common.infra.AppleSecureStorage
import com.bbl.stash.common.infra.SecurePreferenceManager
import com.bbl.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { SecurePreferenceManager() }
    single<SecureStorage> { AppleSecureStorage(get<SecurePreferenceManager>()) }
}
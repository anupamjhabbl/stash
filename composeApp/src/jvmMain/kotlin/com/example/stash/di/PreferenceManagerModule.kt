package com.example.stash.di

import com.example.stash.common.infra.DesktopPreferenceManager
import com.example.stash.common.infra.DesktopSecureStorage
import com.example.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { DesktopPreferenceManager() }
    single<SecureStorage> { DesktopSecureStorage(get<DesktopPreferenceManager>()) }
}
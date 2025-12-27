package com.bbl.stash.di

import com.bbl.stash.common.infra.DesktopPreferenceManager
import com.bbl.stash.common.infra.DesktopSecureStorage
import com.bbl.stash.common.infra.SecureStorage
import org.koin.dsl.module

actual val preferenceManagerModule = module {
    single { DesktopPreferenceManager() }
    single<SecureStorage> { DesktopSecureStorage(get<DesktopPreferenceManager>()) }
}
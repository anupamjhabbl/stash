package com.bbl.stash.di

import com.bbl.stash.common.DeviceIdProvider
import com.bbl.stash.common.IosDeviceIdProvider
import org.koin.dsl.module

actual val iosModule = module {
    single<DeviceIdProvider> { IosDeviceIdProvider() }
}
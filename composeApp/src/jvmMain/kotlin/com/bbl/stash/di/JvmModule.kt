package com.bbl.stash.di

import com.bbl.stash.sync.JvmSyncScheduler
import org.koin.dsl.module

val JvmModule = module {
    single { JvmSyncScheduler(get()) }
}
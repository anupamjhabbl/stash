package com.example.stash.di

import com.example.stash.sync.JvmSyncScheduler
import org.koin.dsl.module

val JvmModule = module {
    single { JvmSyncScheduler(get()) }
}
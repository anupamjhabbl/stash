package com.bbl.stash.di

import com.bbl.stash.common.auth.GoogleAuthentication
import org.koin.dsl.module

val androidModule = module {
    single<GoogleAuthentication> {
        GoogleAuthentication(get())
    }
}
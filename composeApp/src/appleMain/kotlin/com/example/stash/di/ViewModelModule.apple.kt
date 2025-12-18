package com.example.stash.di

import com.example.stash.presentation.viewmodels.MainStashScreenViewModel
import com.example.stash.presentation.viewmodels.StashDockerViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val viewModelModule = module {
    singleOf(::MainStashScreenViewModel)
    singleOf(::StashDockerViewModel)
}
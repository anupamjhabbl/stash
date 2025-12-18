package com.example.stash.di

import com.example.stash.presentation.viewmodels.MainStashScreenViewModel
import com.example.stash.presentation.viewmodels.StashDockerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::MainStashScreenViewModel)
    viewModelOf(::StashDockerViewModel)
}
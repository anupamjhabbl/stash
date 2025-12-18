package com.example.stash.di

import com.example.stash.data.repositoryImpl.StashDataRepositoryImpl
import com.example.stash.domain.repository.StashDataRepository
import com.example.stash.domain.usecase.StashDataUseCase
import org.koin.dsl.module

val commonModule = module {
    single<StashDataRepository> {
        StashDataRepositoryImpl(get())
    }
    single {
        StashDataUseCase(get())
    }
}
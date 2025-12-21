package com.example.stash.di

import com.example.stash.common.Constants
import com.example.stash.data.client.StashClient
import com.example.stash.data.client.createStashClient
import com.example.stash.data.repositoryImpl.StashDataRepositoryImpl
import com.example.stash.data.repositoryImpl.StashRemoteRepositoryImpl
import com.example.stash.domain.repository.StashDataRepository
import com.example.stash.domain.repository.StashRemoteRepository
import com.example.stash.domain.usecase.StashDataUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module

val commonModule = module {
    single<StashDataRepository> {
        StashDataRepositoryImpl(get())
    }
    single {
        StashDataUseCase(get())
    }
    single<StashRemoteRepository> {
        StashRemoteRepositoryImpl(get())
    }
    single<StashClient> {
        val ktorfit = Ktorfit.Builder().baseUrl(Constants.BASE_URL).build()
        ktorfit.createStashClient()
    }
}
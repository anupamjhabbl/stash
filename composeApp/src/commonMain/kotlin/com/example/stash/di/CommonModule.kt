package com.example.stash.di

import com.example.stash.data.client.StashClient
import com.example.stash.data.client.createStashClient
import com.example.stash.data.dao.StashDao
import com.example.stash.data.repositoryImpl.StashDataRepositoryImpl
import com.example.stash.data.repositoryImpl.StashRemoteRepositoryImpl
import com.example.stash.domain.repository.StashDataRepository
import com.example.stash.domain.repository.StashRemoteRepository
import com.example.stash.domain.usecase.StashDataUseCase
import org.koin.dsl.module

val commonModule = module {
    single<StashDataRepository> {
        StashDataRepositoryImpl(get<StashDao>())
    }
    single {
        StashDataUseCase(get<StashDataRepository>())
    }
    single<StashRemoteRepository> {
        StashRemoteRepositoryImpl(get<StashClient>(), get<StashDao>())
    }
    single<StashClient> {
        val ktorfit = InfraProvider.getKtorFitInstance()
        ktorfit.createStashClient()
    }
}
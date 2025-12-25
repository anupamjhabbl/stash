package com.example.stash.di

import com.example.stash.auth.clients.UserAuthClient
import com.example.stash.auth.clients.UserClient
import com.example.stash.auth.clients.createUserAuthClient
import com.example.stash.auth.clients.createUserClient
import com.example.stash.auth.repositories.AuthPreferencesRepository
import com.example.stash.auth.repositories.GetProfileRepository
import com.example.stash.auth.repositories.UserAuthRepository
import com.example.stash.auth.repositoryImpl.AuthPreferencesNetwork
import com.example.stash.auth.repositoryImpl.GetProfileNetwork
import com.example.stash.auth.repositoryImpl.UserAuthNetwork
import com.example.stash.auth.usecases.AuthPreferencesUseCase
import com.example.stash.auth.usecases.ProfileUseCase
import com.example.stash.auth.usecases.UserAuthUseCase
import com.example.stash.common.infra.InfraProvider
import com.example.stash.common.infra.PreferenceManager
import com.example.stash.common.infra.SecureStorage
import com.example.stash.common.infra.TokenAuthenticator
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
        val ktorfit = InfraProvider.getKtorFitInstance(TokenAuthenticator(get(), get()))
        ktorfit.createStashClient()
    }

    single<AuthPreferencesUseCase> {
        AuthPreferencesUseCase(get<AuthPreferencesRepository>())
    }

    single<PreferenceManager> {
        PreferenceManager()
    }

    single<AuthPreferencesRepository> {
        AuthPreferencesNetwork(get<SecureStorage>(), get<PreferenceManager>())
    }

    single<UserAuthUseCase> {
        UserAuthUseCase(get<UserAuthRepository>())
    }

    single<UserAuthRepository> {
        UserAuthNetwork(get<UserAuthClient>())
    }

    single<UserAuthClient> {
        val ktorfit = InfraProvider.getKtorFitInstance()
        ktorfit.createUserAuthClient()
    }

    single<UserClient> {
        val ktorfit = InfraProvider.getKtorFitInstance(TokenAuthenticator(get(), get()))
        ktorfit.createUserClient()
    }

    single<GetProfileRepository> {
        GetProfileNetwork(get<UserClient>())
    }

    single<ProfileUseCase> {
        ProfileUseCase(get<GetProfileRepository>())
    }
}
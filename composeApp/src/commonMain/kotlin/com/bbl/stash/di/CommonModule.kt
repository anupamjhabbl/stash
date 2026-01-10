package com.bbl.stash.di

import com.bbl.stash.auth.clients.UserAuthClient
import com.bbl.stash.auth.clients.UserClient
import com.bbl.stash.auth.clients.createUserAuthClient
import com.bbl.stash.auth.clients.createUserClient
import com.bbl.stash.auth.repositories.AuthPreferencesRepository
import com.bbl.stash.auth.repositories.GetProfileRepository
import com.bbl.stash.auth.repositories.UserAuthRepository
import com.bbl.stash.auth.repositoryImpl.AuthPreferencesNetwork
import com.bbl.stash.auth.repositoryImpl.GetProfileNetwork
import com.bbl.stash.auth.repositoryImpl.UserAuthNetwork
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.ProfileUseCase
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.DeviceIdProvider
import com.bbl.stash.common.infra.InfraProvider
import com.bbl.stash.common.infra.PlatformInfraProvider
import com.bbl.stash.common.infra.PreferenceManager
import com.bbl.stash.common.infra.SecureStorage
import com.bbl.stash.common.infra.TokenAuthenticator
import com.bbl.stash.data.client.SerpImageClient
import com.bbl.stash.data.client.StashClient
import com.bbl.stash.data.client.createSerpImageClient
import com.bbl.stash.data.client.createStashClient
import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.data.repositoryImpl.SerpImageNetwork
import com.bbl.stash.data.repositoryImpl.StashDataRepositoryImpl
import com.bbl.stash.data.repositoryImpl.StashRemoteRepositoryImpl
import com.bbl.stash.domain.repository.SerpImageRepository
import com.bbl.stash.domain.repository.StashDataRepository
import com.bbl.stash.domain.repository.StashRemoteRepository
import com.bbl.stash.domain.usecase.SerpImageUseCase
import com.bbl.stash.domain.usecase.StashDataUseCase
import com.bbl.stash.domain.usecase.StashSyncUseCase
import com.bbl.stash.sync.StashSyncManager
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single<StashDataRepository> {
        StashDataRepositoryImpl(get<StashDao>())
    }

    single {
        StashDataUseCase(get<StashDataRepository>())
    }

    single<StashRemoteRepository> {
        StashRemoteRepositoryImpl(get<StashClient>())
    }

    single<StashClient> {
        val ktorfit = InfraProvider.getKtorFitInstance(get<HttpClient>(named(InfraProvider.AUTH)))
        ktorfit.createStashClient()
    }

    single<TokenAuthenticator> { TokenAuthenticator(get<AuthPreferencesUseCase>(), get<UserAuthUseCase>()) }

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
        val ktorfit = InfraProvider.getKtorFitInstance(get<HttpClient>(named(InfraProvider.NO_AUTH)))
        ktorfit.createUserAuthClient()
    }

    single<UserClient> {
        val ktorfit = InfraProvider.getKtorFitInstance(get<HttpClient>(named(InfraProvider.AUTH)))
        ktorfit.createUserClient()
    }

    single<GetProfileRepository> {
        GetProfileNetwork(get<UserClient>())
    }

    single<ProfileUseCase> {
        ProfileUseCase(get<GetProfileRepository>())
    }

    single<StashSyncUseCase> {
        StashSyncUseCase(get<StashRemoteRepository>(), get<StashDataRepository>(), get<AuthPreferencesUseCase>(), get<SerpImageUseCase>())
    }

    single<StashSyncManager> {
        StashSyncManager(get<StashSyncUseCase>())
    }

    single<SerpImageUseCase> {
        SerpImageUseCase(get())
    }

    single<SerpImageRepository> {
        SerpImageNetwork(get())
    }

    single<SerpImageClient> {
        val ktorfit = InfraProvider.getKtorFitInstanceForSerp(get<HttpClient>(named(InfraProvider.NO_AUTH)))
        ktorfit.createSerpImageClient()
    }

    single<HttpClient>(named(InfraProvider.AUTH)) {
        InfraProvider.getHttpClient(PlatformInfraProvider.getHttpClientEngine(), get<TokenAuthenticator>(), get<DeviceIdProvider>())
    }

    single<HttpClient>(named(InfraProvider.NO_AUTH)) {
        InfraProvider.getHttpClient(PlatformInfraProvider.getHttpClientEngine(), get<DeviceIdProvider>())
    }
}
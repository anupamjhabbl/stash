package com.example.stash.data.repositoryImpl

import com.example.stash.data.client.StashClient
import com.example.stash.domain.repository.StashRemoteRepository

class StashRemoteRepositoryImpl(
    private val stashClient: StashClient
): StashRemoteRepository {
    override suspend fun updateCategoriesFromRemote() {

    }

    override suspend fun updateItemsFromRemote() {

    }

    override suspend fun updateCategoriesToRemote() {

    }

    override suspend fun updateItemsToRemote() {

    }
}
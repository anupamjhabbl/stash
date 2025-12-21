package com.example.stash.sync

import com.example.stash.domain.repository.StashRemoteRepository

class StashDataSync(
    private val stashRemoteRepository: StashRemoteRepository
) {
    suspend fun syncData() {
        stashRemoteRepository.updateCategoriesFromRemote()
        stashRemoteRepository.updateItemsFromRemote()
        stashRemoteRepository.updateCategoriesToRemote()
        stashRemoteRepository.updateItemsToRemote()
    }
}
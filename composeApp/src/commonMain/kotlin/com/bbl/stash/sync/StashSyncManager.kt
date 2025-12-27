package com.bbl.stash.sync

import com.bbl.stash.domain.repository.StashRemoteRepository

class StashSyncManager(
    private val stashRemoteRepository: StashRemoteRepository
) {
    suspend fun syncData() {
        stashRemoteRepository.updateCategoriesFromRemote()
        stashRemoteRepository.updateItemsFromRemote()
        stashRemoteRepository.updateCategoriesToRemote()
        stashRemoteRepository.updateItemsToRemote()
    }
}
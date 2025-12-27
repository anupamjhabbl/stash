package com.bbl.stash.sync

import com.bbl.stash.domain.repository.StashRemoteRepository

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
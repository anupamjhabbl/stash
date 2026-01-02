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
        stashRemoteRepository.deleteCategoryToRemote()
        stashRemoteRepository.deleteItemToRemote()
    }
//    I have decided that we will not sync deleted from remote so that user don't see something delete suddenly if user
//    deletes from a device only then he will see it deleted if ever decides to change this behaviour just add delete sync from
//    remote for both category and items. Remote will be sent deleted data so that it don't send the deleted data in next syncs or new logins or fresh installs.
}
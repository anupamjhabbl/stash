package com.bbl.stash.sync

import com.bbl.stash.domain.usecase.StashSyncUseCase

class StashSyncManager(
    private val stashSyncUseCase: StashSyncUseCase
) {
    suspend fun syncData() {
        stashSyncUseCase.putImage()
        stashSyncUseCase.updateCategoriesFromRemote()
        stashSyncUseCase.updateItemsFromRemote()
        stashSyncUseCase.updateCategoriesToRemote()
        stashSyncUseCase.updateItemsToRemote()
        stashSyncUseCase.deleteCategoryToRemote()
        stashSyncUseCase.deleteItemToRemote()
    }
}
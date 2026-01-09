package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.data.client.StashClient
import com.bbl.stash.data.client.model.StashCategory
import com.bbl.stash.data.client.model.StashCategoryBatch
import com.bbl.stash.data.client.model.StashDeleteCategories
import com.bbl.stash.data.client.model.StashDeleteItems
import com.bbl.stash.data.client.model.StashItem
import com.bbl.stash.data.client.model.StashItemBatch
import com.bbl.stash.domain.repository.StashRemoteRepository

class StashRemoteRepositoryImpl(
    private val stashClient: StashClient
): StashRemoteRepository {
    override suspend fun getCategoriesFromRemote(): List<StashCategory> {
        return BaseResponse.processResponse { stashClient.getCategories() }?.stashCategoryList ?: emptyList()
    }

    override suspend fun getItemsFromRemote(): List<StashItem> {
        return BaseResponse.processResponse { stashClient.getItems() }?.stashItemList ?: emptyList()
    }

    override suspend fun updateCategoriesToRemote(filteredCategoryWithSync: List<StashCategory>) {
        BaseResponse.processResponse {
            stashClient.updateCategories(
                StashCategoryBatch(
                    stashCategoryList = filteredCategoryWithSync
                )
            )
        }
    }

    override suspend fun updateItemsToRemote(filteredItemsWithSyncForUpdate: List<StashItem>) {
        BaseResponse.processResponse {
            stashClient.updateItems(
                StashItemBatch(
                    stashItemList = filteredItemsWithSyncForUpdate
                )
            )
        }
    }

    override suspend fun deleteCategoryToRemote(stashDeleteCategories: StashDeleteCategories) {
        BaseResponse.processResponse {
            stashClient.deleteCategories(
                stashDeleteCategories
            )
        }
    }

    override suspend fun deleteItemToRemote(stashDeleteItems: StashDeleteItems) {
        BaseResponse.processResponse {
            stashClient.deleteItems(
                stashDeleteItems
            )
        }
    }
}
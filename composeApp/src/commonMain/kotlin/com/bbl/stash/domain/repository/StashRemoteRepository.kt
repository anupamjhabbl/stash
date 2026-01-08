package com.bbl.stash.domain.repository

import com.bbl.stash.data.client.model.StashCategory
import com.bbl.stash.data.client.model.StashDeleteCategories
import com.bbl.stash.data.client.model.StashDeleteItems
import com.bbl.stash.data.client.model.StashItem

interface StashRemoteRepository {
    suspend fun getCategoriesFromRemote(): List<StashCategory>
    suspend fun getItemsFromRemote(): List<StashItem>
    suspend fun updateCategoriesToRemote(filteredCategoryWithSync: List<StashCategory>)
    suspend fun updateItemsToRemote(filteredItemsWithSyncForUpdate: List<StashItem>)
    suspend fun deleteCategoryToRemote(stashDeleteCategories: StashDeleteCategories)
    suspend fun deleteItemToRemote(stashDeleteItems: StashDeleteItems)
}
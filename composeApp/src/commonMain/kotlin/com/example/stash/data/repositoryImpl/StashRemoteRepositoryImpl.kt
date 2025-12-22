package com.example.stash.data.repositoryImpl

import com.example.stash.data.client.StashClient
import com.example.stash.data.client.model.StashCategory
import com.example.stash.data.client.model.StashCategoryBatch
import com.example.stash.data.client.model.StashItem
import com.example.stash.data.client.model.StashItemBatch
import com.example.stash.data.dao.StashDao
import com.example.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.example.stash.domain.model.entity.StashCategorySync
import com.example.stash.domain.model.entity.StashItemSync
import com.example.stash.domain.model.entity.SyncStatus
import com.example.stash.domain.repository.StashRemoteRepository

class StashRemoteRepositoryImpl(
    private val stashClient: StashClient,
    private val stashDao: StashDao
): StashRemoteRepository {
    override suspend fun updateCategoriesFromRemote() {
        val categoriesFromRemote = stashClient.getCategories().stashCategoryList
        val categoriesFromLocal = stashDao.getCategoriesWithSyncData()
        val localCategoriesById = categoriesFromLocal.associateBy {
            it.stashCategory.categoryId
        }
        val categoryListToUpdate = mutableListOf<com.example.stash.domain.model.entity.StashCategory>()
        val categorySyncListToUpdate = mutableListOf<StashCategorySync>()

        categoriesFromRemote.forEach { remote ->

            val remoteCategory = remote.stashCategory
            val local = localCategoriesById[remoteCategory.categoryId]

            val stashCategory = com.example.stash.domain.model.entity.StashCategory(
                categoryId = remoteCategory.categoryId,
                categoryName = remoteCategory.categoryName
            )

            when {
                local == null -> {
                    categoryListToUpdate += stashCategory
                    categorySyncListToUpdate += StashCategorySync(
                        stashCategoryId = stashCategory.categoryId,
                        syncStatus = SyncStatus.COMPLETED.status,
                        lastUpdated = remote.lastUpdated
                    )
                }

                local.syncData.lastUpdated < remote.lastUpdated -> {
                    categoryListToUpdate += stashCategory
                    categorySyncListToUpdate += StashCategorySync(
                        id = local.syncData.id,
                        stashCategoryId = stashCategory.categoryId,
                        syncStatus = SyncStatus.COMPLETED.status,
                        lastUpdated = remote.lastUpdated
                    )
                }
            }
        }

        if (categoryListToUpdate.isNotEmpty()) {
            stashDao.insertStashCategoryList(categoryListToUpdate)
        }

        if (categorySyncListToUpdate.isNotEmpty()) {
            stashDao.insertStashCategorySyncList(categorySyncListToUpdate)
        }
    }

    override suspend fun updateItemsFromRemote() {
        val itemsFromRemote = stashClient.getItems().stashItemList
        val itemsFromLocal = stashDao.getItemsWithSyncData()
        val localItemsById = itemsFromLocal.associateBy {
            it.stashItem.stashItemId
        }
        val itemsListToUpdate = mutableListOf<com.example.stash.domain.model.entity.StashItem>()
        val itemsSyncListToUpdate = mutableListOf<StashItemSync>()

        itemsFromRemote.forEach { remote ->

            val remoteItem = remote.stashItem
            val local = localItemsById[remoteItem.stashItemId]

            val stashItem = com.example.stash.domain.model.entity.StashItem(
                stashItemId = remoteItem.stashItemId,
                stashCategoryId = remoteItem.stashCategoryId,
                stashItemName = remoteItem.stashItemName,
                stashItemUrl = remoteItem.stashItemUrl,
                stashItemRating = remoteItem.stashItemRating,
                stashItemCompleted = remoteItem.stashItemCompleted
            )

            when {
                local == null -> {
                    itemsListToUpdate += stashItem
                    itemsSyncListToUpdate += StashItemSync(
                        stashItemId = stashItem.stashItemId,
                        syncStatus = SyncStatus.COMPLETED.status,
                        lastUpdated = remote.lastUpdated
                    )
                }

                local.syncData.lastUpdated < remote.lastUpdated -> {
                    itemsListToUpdate += stashItem
                    itemsSyncListToUpdate += StashItemSync(
                        id = local.syncData.id,
                        stashItemId = stashItem.stashItemId,
                        syncStatus = SyncStatus.COMPLETED.status,
                        lastUpdated = remote.lastUpdated
                    )
                }
            }
        }
        if (itemsListToUpdate.isNotEmpty()) {
            stashDao.insertStashItemList(itemsListToUpdate)
        }

        if (itemsSyncListToUpdate.isNotEmpty()) {
            stashDao.insertStashItemSyncList(itemsSyncListToUpdate)
        }
    }

    override suspend fun updateCategoriesToRemote() {
        val categoryWithSync = stashDao.getCategoriesWithSyncData()
        val filteredCategoryWithSync = categoryWithSync.filter { it.syncData.syncStatus == SyncStatus.PENDING.status }
        val filteredCategoryWithSyncForUpdate = filteredCategoryWithSync.map {
            StashCategory(
                it.stashCategory.mapToDto(),
                it.syncData.lastUpdated
            )
        }
        stashClient.updateCategories(
            StashCategoryBatch(
                stashCategoryList = filteredCategoryWithSyncForUpdate
            )
        )
        stashDao.insertStashCategorySyncList(
            filteredCategoryWithSync.map {
                StashCategorySync(
                    id = it.syncData.id,
                    stashCategoryId = it.stashCategory.categoryId,
                    syncStatus = SyncStatus.COMPLETED.status,
                    lastUpdated = it.syncData.lastUpdated
                )
            }
        )
    }

    override suspend fun updateItemsToRemote() {
        val categoryWithSync = stashDao.getItemsWithSyncData()
        val filteredCategoryWithSync =  categoryWithSync.filter { it.syncData.syncStatus == SyncStatus.PENDING.status }
        val filteredCategoryWithSyncForUpdate = filteredCategoryWithSync.map {
            StashItem(
                it.stashItem.mapToDto(),
                it.syncData.lastUpdated
            )
        }
        stashClient.updateItems(
            StashItemBatch(
                stashItemList = filteredCategoryWithSyncForUpdate
            )
        )
        stashDao.insertStashItemSyncList(
            filteredCategoryWithSync.map {
                StashItemSync(
                    id = it.syncData.id,
                    stashItemId = it.stashItem.stashItemId,
                    syncStatus = SyncStatus.COMPLETED.status,
                    lastUpdated = it.syncData.lastUpdated
                )
            }
        )
    }
}
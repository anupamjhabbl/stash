package com.example.stash.data.repositoryImpl

import com.example.stash.auth.usecases.AuthPreferencesUseCase
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
    private val stashDao: StashDao,
    private val authPreferencesUseCase: AuthPreferencesUseCase
): StashRemoteRepository {
    override suspend fun updateCategoriesFromRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoriesFromRemote = stashClient.getCategories().stashCategoryList
        val categoriesFromLocal = stashDao.getCategoriesWithSyncData(loggedUserId)
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
                categoryName = remoteCategory.categoryName,
                userId = loggedUserId
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
                        stashCategorySyncId = local.syncData.stashCategorySyncId,
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
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val itemsFromRemote = stashClient.getItems().stashItemList
        val itemsFromLocal = stashDao.getItemsWithSyncData(loggedUserId)
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
                stashItemCompleted = remoteItem.stashItemCompleted,
                userId = loggedUserId
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
                        stashItemSyncId = local.syncData.stashItemSyncId,
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
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoryWithSync = stashDao.getCategoriesWithSyncData(
            loggedUserId
        )
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
                    stashCategorySyncId = it.syncData.stashCategorySyncId,
                    stashCategoryId = it.stashCategory.categoryId,
                    syncStatus = SyncStatus.COMPLETED.status,
                    lastUpdated = it.syncData.lastUpdated
                )
            }
        )
    }

    override suspend fun updateItemsToRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val itemsWithSync = stashDao.getItemsWithSyncData(loggedUserId)
        val filteredItemsWithSync =  itemsWithSync.filter { it.syncData.syncStatus == SyncStatus.PENDING.status }
        val filteredItemsWithSyncForUpdate = filteredItemsWithSync.map {
            StashItem(
                it.stashItem.mapToDto(),
                it.syncData.lastUpdated
            )
        }
        stashClient.updateItems(
            StashItemBatch(
                stashItemList = filteredItemsWithSyncForUpdate
            )
        )
        stashDao.insertStashItemSyncList(
            filteredItemsWithSync.map {
                StashItemSync(
                    stashItemSyncId = it.syncData.stashItemSyncId,
                    stashItemId = it.stashItem.stashItemId,
                    syncStatus = SyncStatus.COMPLETED.status,
                    lastUpdated = it.syncData.lastUpdated
                )
            }
        )
    }
}
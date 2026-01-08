package com.bbl.stash.data.repositoryImpl

import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.common.BuildConfigValues
import com.bbl.stash.data.client.StashClient
import com.bbl.stash.data.client.model.StashCategory
import com.bbl.stash.data.client.model.StashCategoryBatch
import com.bbl.stash.data.client.model.StashDeleteCategories
import com.bbl.stash.data.client.model.StashDeleteItems
import com.bbl.stash.data.client.model.StashItem
import com.bbl.stash.data.client.model.StashItemBatch
import com.bbl.stash.data.dao.StashDao
import com.bbl.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItemSync
import com.bbl.stash.domain.model.entity.SyncStatus
import com.bbl.stash.domain.repository.StashRemoteRepository
import com.bbl.stash.domain.usecase.SerpImageUseCase

class StashRemoteRepositoryImpl(
    private val stashClient: StashClient,
    private val stashDao: StashDao,
    private val authPreferencesUseCase: AuthPreferencesUseCase,
    private val serpImageUseCase: SerpImageUseCase
): StashRemoteRepository {
    override suspend fun updateCategoriesFromRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoriesFromRemote = stashClient.getCategories().data?.stashCategoryList ?: emptyList()
        val categoriesFromLocal = stashDao.getCategoriesWithSyncData(loggedUserId)
        val localCategoriesById = categoriesFromLocal.associateBy {
            it.stashCategory.categoryId
        }
        val categoryListToUpdate = mutableListOf<com.bbl.stash.domain.model.entity.StashCategory>()
        val categorySyncListToUpdate = mutableListOf<StashCategorySync>()

        categoriesFromRemote.forEach { remote ->
            val remoteCategory = remote.stashCategory
            val local = localCategoriesById[remoteCategory.categoryId]

            val stashCategory = com.bbl.stash.domain.model.entity.StashCategory(
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
        val itemsFromRemote = stashClient.getItems().data?.stashItemList ?: emptyList()
        val itemsFromLocal = stashDao.getItemsWithSyncData(loggedUserId)
        val localItemsById = itemsFromLocal.associateBy {
            it.stashItem.stashItemId
        }
        val itemsListToUpdate = mutableListOf<com.bbl.stash.domain.model.entity.StashItem>()
        val itemsSyncListToUpdate = mutableListOf<StashItemSync>()

        itemsFromRemote.forEach { remote ->

            val remoteItem = remote.stashItem
            val local = localItemsById[remoteItem.stashItemId]

            val stashItem = com.bbl.stash.domain.model.entity.StashItem(
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

    override suspend fun deleteCategoryToRemote() {
        val categoriesToDelete = stashDao.getDeletedCategory()
        stashClient.deleteCategories(
            StashDeleteCategories(
                categoriesToDelete.map { it.categoryId }
            )
        )
        stashDao.clearDeletedCategory()
    }

    override suspend fun deleteItemToRemote() {
        val itemsToDelete = stashDao.getDeletedItem()
        stashClient.deleteItems(
            StashDeleteItems(
                itemsToDelete.map { it.itemId }
            )
        )
        stashDao.clearDeletedItem()
    }

    override suspend fun putImage() {
        val itemList = stashDao.getCategoriesWithItem()
        itemList.forEach { categoryWithItems ->
            categoryWithItems.items
                .filter {
                    it.stashItemUrl.isEmpty()
                }.forEach {
                    try {
                        val imageUri = serpImageUseCase.getImageUri("${categoryWithItems.category.categoryName} ${it.stashItemName}", BuildConfigValues.getSerpApiKey())
                        if (!imageUri.isNullOrEmpty()) {
                            stashDao.insertStashItem(it.copy(stashItemUrl = imageUri))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}
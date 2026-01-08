package com.bbl.stash.domain.usecase

import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.common.BuildConfigValues
import com.bbl.stash.data.client.model.StashCategory
import com.bbl.stash.data.client.model.StashDeleteCategories
import com.bbl.stash.data.client.model.StashDeleteItems
import com.bbl.stash.data.client.model.StashItem
import com.bbl.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItemSync
import com.bbl.stash.domain.model.entity.SyncStatus
import com.bbl.stash.domain.repository.StashDataRepository
import com.bbl.stash.domain.repository.StashRemoteRepository

class StashSyncUseCase(
    private val stashRemoteRepository: StashRemoteRepository,
    private val stashDataRepository: StashDataRepository,
    private val authPreferencesUseCase: AuthPreferencesUseCase,
    private val serpImageUseCase: SerpImageUseCase
) {
    suspend fun updateCategoriesFromRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoriesFromRemote = stashRemoteRepository.getCategoriesFromRemote()
        val categoriesFromLocal = stashDataRepository.getCategoriesFromLocal(loggedUserId)
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
            stashDataRepository.insertStashCategoryList(categoryListToUpdate)
        }

        if (categorySyncListToUpdate.isNotEmpty()) {
            stashDataRepository.insertStashCategorySyncList(categorySyncListToUpdate)
        }
    }

    suspend fun updateItemsFromRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val itemsFromRemote = stashRemoteRepository.getItemsFromRemote()
        val itemsFromLocal = stashDataRepository.getItemsFromLocal(loggedUserId)
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
            stashDataRepository.insertStashItemList(itemsListToUpdate)
        }

        if (itemsSyncListToUpdate.isNotEmpty()) {
            stashDataRepository.insertStashItemSyncList(itemsSyncListToUpdate)
        }
    }

    suspend fun updateCategoriesToRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoryWithSync = stashDataRepository.getCategoriesFromLocal(
            loggedUserId
        )
        val filteredCategoryWithSync = categoryWithSync.filter { it.syncData.syncStatus == SyncStatus.PENDING.status }
        val filteredCategoryWithSyncForUpdate = filteredCategoryWithSync.map {
            StashCategory(
                it.stashCategory.mapToDto(),
                it.syncData.lastUpdated
            )
        }
        if (filteredCategoryWithSync.isEmpty()) return
        stashRemoteRepository.updateCategoriesToRemote(filteredCategoryWithSyncForUpdate)
        stashDataRepository.insertStashCategorySyncList(filteredCategoryWithSync.map {
            StashCategorySync(
                stashCategorySyncId = it.syncData.stashCategorySyncId,
                stashCategoryId = it.stashCategory.categoryId,
                syncStatus = SyncStatus.COMPLETED.status,
                lastUpdated = it.syncData.lastUpdated
            )
        })
    }

    suspend fun updateItemsToRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val itemsWithSync = stashDataRepository.getItemsFromLocal(loggedUserId)
        val filteredItemsWithSync =  itemsWithSync.filter { it.syncData.syncStatus == SyncStatus.PENDING.status }
        val filteredItemsWithSyncForUpdate = filteredItemsWithSync.map {
            StashItem(
                it.stashItem.mapToDto(),
                it.syncData.lastUpdated
            )
        }
        if (filteredItemsWithSyncForUpdate.isEmpty()) return
        stashRemoteRepository.updateItemsToRemote(filteredItemsWithSyncForUpdate)
        stashDataRepository.insertStashItemSyncList(
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

    suspend fun deleteCategoryToRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val categoriesToDelete = stashDataRepository.getDeletedCategory(loggedUserId)
        if (categoriesToDelete.isEmpty()) return
        stashRemoteRepository.deleteCategoryToRemote(
            StashDeleteCategories(
                categoriesToDelete.map { it.categoryId }
            )
        )
        stashDataRepository.clearDeletedCategory(loggedUserId)
    }

    suspend fun deleteItemToRemote() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        val itemsToDelete = stashDataRepository.getDeletedItem(loggedUserId)
        if (itemsToDelete.isEmpty()) return
        stashRemoteRepository.deleteItemToRemote(
            StashDeleteItems(
                itemsToDelete.map { it.itemId }
            )
        )
        stashDataRepository.clearDeletedItem(loggedUserId)
    }

    suspend fun putImage() {
        val itemList = stashDataRepository.getCategoriesWithItem()
        itemList.forEach { categoryWithItems ->
            categoryWithItems.items
                .filter {
                    it.stashItemUrl.isEmpty()
                }.forEach {
                    try {
                        val imageUri = serpImageUseCase.getImageUri("${categoryWithItems.category.categoryName} ${it.stashItemName}", BuildConfigValues.getSerpApiKey())
                        if (!imageUri.isNullOrEmpty()) {
                            stashDataRepository.insertStashItem(it.copy(stashItemUrl = imageUri))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}
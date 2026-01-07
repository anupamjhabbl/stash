package com.bbl.stash.domain.repository

interface StashRemoteRepository {
    suspend fun updateCategoriesFromRemote()
    suspend fun updateItemsFromRemote()
    suspend fun updateCategoriesToRemote()
    suspend fun updateItemsToRemote()
    suspend fun deleteCategoryToRemote()
    suspend fun deleteItemToRemote()
    suspend fun putImage()
}
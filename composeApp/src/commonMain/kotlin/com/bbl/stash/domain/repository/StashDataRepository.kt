package com.bbl.stash.domain.repository

import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.domain.model.entity.CategoryWithItems
import com.bbl.stash.domain.model.entity.CategoryWithSync
import com.bbl.stash.domain.model.entity.DeletedCategory
import com.bbl.stash.domain.model.entity.DeletedItem
import com.bbl.stash.domain.model.entity.StashCategory
import com.bbl.stash.domain.model.entity.StashCategorySync
import com.bbl.stash.domain.model.entity.StashItem
import com.bbl.stash.domain.model.entity.StashItemSync
import com.bbl.stash.domain.model.entity.StashItemWithSync
import kotlinx.coroutines.flow.Flow

interface StashDataRepository {
    fun getCategoryDataWithItems(loggedUserId: String): Flow<List<StashCategoryWithItem>>

    suspend fun addStashCategory(categoryId: String?, categoryName: String, loggedUserId: String)

    fun getCategoryDataWithItemsForId(stashCategoryId: String, loggedUserId: String): Flow<StashCategoryWithItem>

    suspend fun addStashItem(
        loggedUserId: String,
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    )

    suspend fun deleteStashCategory(categoryId: String)

    suspend fun deleteStashItem(itemId: String)

    suspend fun getCategoriesFromLocal(loggedUserId: String): List<CategoryWithSync>

    suspend fun insertStashCategoryList(categoryListToUpdate: List<StashCategory>)

    suspend fun insertStashCategorySyncList(categorySyncListToUpdate: List<StashCategorySync>)

    suspend fun getItemsFromLocal(loggedUserId: String): List<StashItemWithSync>

    suspend fun insertStashItemList(itemsListToUpdate: List<StashItem>)

    suspend fun insertStashItemSyncList(itemsSyncListToUpdate: List<StashItemSync>)

    suspend fun getDeletedCategory(): List<DeletedCategory>

    suspend fun clearDeleteRepository()

    suspend fun getDeletedItem(): List<DeletedItem>

    suspend fun clearDeletedItem()

    suspend fun getCategoriesWithItem(): List<CategoryWithItems>

    suspend fun insertStashItem(stashItem: StashItem)
}
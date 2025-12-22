package com.example.stash.data.repositoryImpl

import com.example.stash.data.dao.StashDao
import com.example.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.model.entity.StashCategory
import com.example.stash.domain.model.entity.StashCategorySync
import com.example.stash.domain.model.entity.StashItemSync
import com.example.stash.domain.model.entity.SyncStatus
import com.example.stash.domain.repository.StashDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StashDataRepositoryImpl(
    private val stashDao: StashDao
): StashDataRepository {
    override fun getCategoryDataWithItems(): Flow<List<StashCategoryWithItem>> {
        return stashDao.getCategoriesWithItems().map { stashCategoryList ->
            stashCategoryList.map { stashCategory ->
                stashCategory.mapToDto()
            }
        }
    }

    override suspend fun addStashCategory(categoryName: String) {
        val stashCategory = StashCategory(categoryName = categoryName)
        stashDao.insertStashCategory(stashCategory)
        stashDao.insertStashCategorySync(
            StashCategorySync(
                stashCategoryId = stashCategory.categoryId,
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }

    override fun getCategoryDataWithItemsForId(stashCategoryId: String): Flow<StashCategoryWithItem> {
        val stashCategoryWithItem = stashDao.getCategoryWithItems(stashCategoryId)
        return stashCategoryWithItem.map { stashCategory ->
            stashCategory.mapToDto()
        }
    }

    override suspend fun addStashItem(
        stashItemId: String?,
        stashCategoryId: String,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    ) {
        val stashItem = if (stashItemId != null) {
            com.example.stash.domain.model.entity.StashItem(
                stashItemId = stashItemId,
                stashCategoryId = stashCategoryId,
                stashItemName = stashItemName,
                stashItemUrl = stashItemUrl,
                stashItemRating = stashItemRating,
                stashItemCompleted = itemCompletedStatus
            )
        } else {
            com.example.stash.domain.model.entity.StashItem(
                stashCategoryId = stashCategoryId,
                stashItemName = stashItemName,
                stashItemUrl = stashItemUrl,
                stashItemRating = stashItemRating,
                stashItemCompleted = itemCompletedStatus
            )
        }
        stashDao.insertStashItem(stashItem)
        stashDao.insertStashItemSync(
            StashItemSync(
                stashItemId = stashItem.stashItemId,
                syncStatus = SyncStatus.PENDING.status
            )
        )
    }
}
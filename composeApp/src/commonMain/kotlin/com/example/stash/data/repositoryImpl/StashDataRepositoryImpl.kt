package com.example.stash.data.repositoryImpl

import com.example.stash.data.dao.StashDao
import com.example.stash.domain.model.dto.EntityToDtoMapper.mapToDto
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.model.entity.StashCategory
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
        stashDao.insertStashCategory(StashCategory(categoryName = categoryName))
    }

    override fun getCategoryDataWithItemsForId(stashCategoryId: Long): Flow<StashCategoryWithItem> {
        val stashCategoryWithItem = stashDao.getCategoryWithItems(stashCategoryId)
        return stashCategoryWithItem.map { stashCategory ->
            stashCategory.mapToDto()
        }
    }

    override suspend fun addStashItem(
        stashItemId: Long?,
        stashCategoryId: Long,
        stashItemName: String,
        stashItemUrl: String,
        stashItemRating: Float,
        itemCompletedStatus: String
    ) {
        if (stashItemId != null) {
            stashDao.insertStashItem(
                com.example.stash.domain.model.entity.StashItem(
                    stashItemId = stashItemId,
                    stashCategoryId = stashCategoryId,
                    stashItemName = stashItemName,
                    stashItemUrl = stashItemUrl,
                    stashItemRating = stashItemRating,
                    stashItemCompleted = itemCompletedStatus
                )
            )
        } else {
            stashDao.insertStashItem(
                com.example.stash.domain.model.entity.StashItem(
                    stashCategoryId = stashCategoryId,
                    stashItemName = stashItemName,
                    stashItemUrl = stashItemUrl,
                    stashItemRating = stashItemRating,
                    stashItemCompleted = itemCompletedStatus
                )
            )
        }
    }
}
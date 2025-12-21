package com.example.stash.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stash.domain.model.entity.CategoryWithItems
import com.example.stash.domain.model.entity.StashCategory
import com.example.stash.domain.model.entity.StashCategorySync
import com.example.stash.domain.model.entity.StashItem
import com.example.stash.domain.model.entity.StashItemSync
import kotlinx.coroutines.flow.Flow

@Dao
interface StashDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategory(stashCategory: StashCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItem(stashItem: StashItem): Long

    @Transaction
    @Query("SELECT * FROM stash_category")
    fun getCategoriesWithItems(): Flow<List<CategoryWithItems>>

    @Transaction
    @Query("SELECT * FROM stash_category WHERE categoryId = :categoryId")
    fun getCategoryWithItems(categoryId: Long): Flow<CategoryWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashCategorySync(stashCategorySync: StashCategorySync): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashItemSync(stashItemSync: StashItemSync): Long
}
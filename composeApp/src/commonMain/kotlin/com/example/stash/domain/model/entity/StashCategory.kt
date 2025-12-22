package com.example.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stash.common.UUIDUtils

@Entity(tableName = "stash_category")
data class StashCategory(
    @PrimaryKey
    val categoryId: String = UUIDUtils.generateUUID(),
    val categoryName: String = ""
)
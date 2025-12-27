package com.bbl.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bbl.stash.common.UUIDUtils

@Entity(tableName = "stash_category")
data class StashCategory(
    @PrimaryKey
    val categoryId: String = UUIDUtils.generateUUID(),
    val userId: String,
    val categoryName: String = ""
)
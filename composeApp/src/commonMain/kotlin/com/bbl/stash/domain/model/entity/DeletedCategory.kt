package com.bbl.stash.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_category")
data class DeletedCategory(
    @PrimaryKey
    val categoryId: String,
    @ColumnInfo(defaultValue = "0")
    val userId: String
)

package com.bbl.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_category")
data class DeletedCategory(
    @PrimaryKey
    val categoryId: String
)

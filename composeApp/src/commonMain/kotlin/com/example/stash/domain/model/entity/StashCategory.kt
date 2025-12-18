package com.example.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stash_category")
data class StashCategory(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val categoryName: String = ""
)
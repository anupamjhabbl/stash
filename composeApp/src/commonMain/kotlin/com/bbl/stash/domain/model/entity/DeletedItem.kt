package com.bbl.stash.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_item")
data class DeletedItem(
    @PrimaryKey
    val itemId: String
)

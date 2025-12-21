package com.example.stash.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
class StashCategory(
    val categoryId: Long = 0,
    val categoryName: String = ""
)
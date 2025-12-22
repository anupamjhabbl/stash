package com.example.stash.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
class StashCategory(
    val categoryId: String,
    val categoryName: String = ""
)
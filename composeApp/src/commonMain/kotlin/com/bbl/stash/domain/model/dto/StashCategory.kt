package com.bbl.stash.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
class StashCategory(
    val categoryId: String,
    val categoryName: String = ""
)
package com.example.stash.domain.model.dto

import com.example.stash.domain.model.entity.StashCategory

data class StashCategoryWithItem(
    val stashCategory: StashCategory? = null,
    val stashItems: List<StashItem> = emptyList()
)

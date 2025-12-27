package com.bbl.stash.domain.model.dto

data class StashCategoryWithItem(
    val stashCategory: StashCategory? = null,
    val stashItems: List<StashItem> = emptyList()
)

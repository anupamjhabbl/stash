package com.bbl.stash.auth.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
)
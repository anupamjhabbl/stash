package com.bbl.stash.auth.entity

import kotlinx.serialization.Serializable

data class UserLoginFormState(
    val email: String = "",
    val password: String = "",
    val isValid: Boolean = false
)

@Serializable
data class UserLoginBody(
    val email: String = "",
    val password: String = ""
)

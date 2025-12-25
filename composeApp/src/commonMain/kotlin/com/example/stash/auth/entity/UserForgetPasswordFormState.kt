package com.example.stash.auth.entity

import kotlinx.serialization.Serializable

data class UserForgetPasswordFormState(
    val email: String = "",
    val isValid: Boolean = false
)

@Serializable
data class UserForgetPasswordBody(
    val email: String = ""
)
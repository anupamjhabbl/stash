package com.bbl.stash.auth.entity

import kotlinx.serialization.Serializable

data class UserResetPasswordFormState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordValid: PasswordStrengthValidityStatus? = null,
    val confirmPasswordValid: Boolean = true
)

@Serializable
data class UserPasswordResetBody(
    val password: String = ""
)
package com.bbl.stash.auth.entity

import kotlinx.serialization.Serializable

data class UserRegisterFormState(
    val email: String = "",
    val password: String = "",
    val userName: String = "",
    val emailValid: Boolean = false,
    val passwordValid: PasswordStrengthValidityStatus? = null,
    val userNameValid: Boolean = false
)

@Serializable
data class UserRegistrationBody(
    val email: String = "",
    val password: String = "",
    val name: String = "",
)

enum class PasswordStrengthValidityStatus(val message: String) {
    VALID("valid"),
    SMALL_LENGTH("Length should be greater than 8"),
    UPPER_CASE_MISSING("At least one Uppercase"),
    LOWER_CASE_MISSING("At least one lowercase"),
    DIGIT_MISSING("At least one digit"),
    SPECIAL_CHARACTER_MISSING("At least one special character")
}
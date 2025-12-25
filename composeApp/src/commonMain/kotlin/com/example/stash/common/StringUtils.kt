package com.example.stash.common

import com.example.stash.auth.entity.PasswordStrengthValidityStatus

object StringUtils {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun String.isValidEmail(): Boolean {
        return this.isNotEmpty() && EMAIL_REGEX.matches(this)
    }

    fun String.isStrongPassword(): PasswordStrengthValidityStatus {
        return if (this.length < 8) {
            PasswordStrengthValidityStatus.SMALL_LENGTH
        } else if (!this.any { it.isUpperCase() }) {
            PasswordStrengthValidityStatus.UPPER_CASE_MISSING
        } else if (!this.any { it.isLowerCase() }) {
            PasswordStrengthValidityStatus.LOWER_CASE_MISSING
        } else if (!this.any { it.isDigit() }) {
            PasswordStrengthValidityStatus.DIGIT_MISSING
        } else if (!this.any { "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".contains(it) }) {
            PasswordStrengthValidityStatus.SPECIAL_CHARACTER_MISSING
        } else {
            PasswordStrengthValidityStatus.VALID
        }
    }

    fun String.isDigitsOnly(): Boolean =
        isNotEmpty() && all { it.isDigit() }
}
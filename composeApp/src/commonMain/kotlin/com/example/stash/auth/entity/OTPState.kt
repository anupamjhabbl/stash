package com.example.stash.auth.entity

import kotlinx.serialization.Serializable

data class OTPState(
    val otpSize: Int,
    val code: List<Int?> = (1..otpSize).map { null },
    val focusedIndex: Int? = null,
    val isValid: Boolean = false
)

@Serializable
data class UserOTPVerifyBody(
    val userId: String = "",
    val otp: String = ""
)
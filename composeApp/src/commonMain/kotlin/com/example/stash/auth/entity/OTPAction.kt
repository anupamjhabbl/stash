package com.example.stash.auth.entity

sealed class OTPAction {
    data class OnChangeFieldFocused(val index: Int): OTPAction()
    data class OnEnterNumber(val index: Int, val number: Int?): OTPAction()
    data object OnKeyboardBack: OTPAction()
}
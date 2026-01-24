package com.bbl.stash.common

import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(
    authPreferencesUseCase: AuthPreferencesUseCase
) {
    private val _isUserLogged: MutableStateFlow<Boolean> = MutableStateFlow(authPreferencesUseCase.isUserLogged())
    val isUserLogged: StateFlow<Boolean> = _isUserLogged.asStateFlow()

    fun updateUserLoggedStatus(isLogged: Boolean) {
        _isUserLogged.value = isLogged
    }
}
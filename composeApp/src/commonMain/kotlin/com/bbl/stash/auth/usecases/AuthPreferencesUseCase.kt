package com.bbl.stash.auth.usecases

import com.bbl.stash.auth.entity.User
import com.bbl.stash.auth.repositories.AuthPreferencesRepository

class AuthPreferencesUseCase(
    val authPreferenceRepository: AuthPreferencesRepository
) {
    fun saveAccessToken(accessToken: String) {
        authPreferenceRepository.saveAccessToken(accessToken)
    }

    fun getAccessToken(): String {
        return authPreferenceRepository.getAccessToken()
    }

    fun saveRefreshToken(refreshToken: String) {
        authPreferenceRepository.saveRefreshToken(refreshToken)
    }

    fun getRefreshToken(): String? {
        return authPreferenceRepository.getRefreshToken()
    }

    fun removeAccessToken() {
        authPreferenceRepository.removeAccessToken()
    }

    fun removeRefreshToken() {
        authPreferenceRepository.removeRefreshToken()
    }

    fun isUserLogged(): Boolean {
        return getLoggedUser() != null
    }

    fun getLoggedUser(): User? {
        return authPreferenceRepository.getLocalUser()
    }

    fun saveLoggedUser(loggedUser: User) {
        authPreferenceRepository.saveLocalUser(loggedUser)
    }

    fun removeUserData() {
        authPreferenceRepository.removeLocalUser()
    }

    fun getLoggedUserId(): String? {
        return authPreferenceRepository.getLoggedUserId()
    }
}
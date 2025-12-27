package com.bbl.stash.auth.repositoryImpl

import com.bbl.stash.auth.entity.User
import com.bbl.stash.auth.repositories.AuthPreferencesRepository
import com.bbl.stash.common.infra.PreferenceManager
import com.bbl.stash.common.infra.SecureStorage

class AuthPreferencesNetwork(
    private val secureStorageManager: SecureStorage,
    private val preferenceManager: PreferenceManager
): AuthPreferencesRepository {
    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }

    override fun saveAccessToken(accessToken: String) {
        secureStorageManager.saveToken(ACCESS_TOKEN, accessToken)
    }

    override fun getAccessToken(): String {
        return secureStorageManager.getToken(ACCESS_TOKEN) ?: ""
    }

    override fun saveRefreshToken(refreshToken: String) {
        secureStorageManager.saveToken(REFRESH_TOKEN, refreshToken)
    }

    override fun getRefreshToken(): String? {
        return secureStorageManager.getToken(REFRESH_TOKEN)
    }

    override fun removeAccessToken() {
        secureStorageManager.remove(ACCESS_TOKEN)
    }

    override fun removeRefreshToken() {
        secureStorageManager.remove(REFRESH_TOKEN)
    }

    override fun isUserLogged(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    override fun clearUserData() {
        secureStorageManager.clear()
    }

    override fun saveLocalUser(loggedUser: User) {
        preferenceManager.saveLocalUser(loggedUser)
    }

    override fun getLocalUser(): User? {
        return preferenceManager.getLocalUser()
    }

    override fun removeLocalUser() {
        preferenceManager.removeLocalUser()
    }

    override fun getLoggedUserId(): String? {
        return getLocalUser()?.id
    }
}
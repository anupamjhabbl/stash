package com.bbl.stash.auth.repositories

import com.bbl.stash.auth.entity.User

interface AuthPreferencesRepository {
    fun isUserLogged(): Boolean
    fun removeRefreshToken()
    fun removeAccessToken()
    fun getRefreshToken(): String?
    fun saveRefreshToken(refreshToken: String)
    fun getAccessToken(): String
    fun saveAccessToken(accessToken: String)
    fun clearUserData()
    fun saveLocalUser(loggedUser: User)
    fun getLocalUser(): User?
    fun removeLocalUser()
    fun getLoggedUserId(): String?
}
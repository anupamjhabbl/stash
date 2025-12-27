package com.bbl.stash.auth.repositories

import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.User

interface GetProfileRepository {
    suspend fun getUser(userId: String): BaseResponse<User>
    suspend fun getLocalUser(): BaseResponse<User>
    suspend fun logoutUser(): BaseResponse<String>
}
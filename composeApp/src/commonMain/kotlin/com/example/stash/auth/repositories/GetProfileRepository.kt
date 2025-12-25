package com.example.stash.auth.repositories

import com.example.stash.auth.entity.BaseResponse
import com.example.stash.auth.entity.User

interface GetProfileRepository {
    suspend fun getUser(userId: String): BaseResponse<User>
    suspend fun getLocalUser(): BaseResponse<User>
    suspend fun logoutUser(): BaseResponse<String>
}
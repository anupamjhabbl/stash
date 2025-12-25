package com.example.stash.auth.repositoryImpl

import com.example.stash.auth.clients.UserClient
import com.example.stash.auth.entity.BaseResponse
import com.example.stash.auth.entity.User
import com.example.stash.auth.repositories.GetProfileRepository

class GetProfileNetwork(
    private val userClient: UserClient
): GetProfileRepository {
    override suspend fun getUser(userId: String): BaseResponse<User> {
        return userClient.getUser(userId)
    }

    override suspend fun getLocalUser(): BaseResponse<User> {
        return userClient.getLocalUser()
    }

    override suspend fun logoutUser(): BaseResponse<String> {
        return userClient.logoutUser()
    }
}

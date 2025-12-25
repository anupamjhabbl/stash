package com.example.stash.auth.usecases

import com.example.stash.auth.entity.BaseResponse
import com.example.stash.auth.entity.User
import com.example.stash.auth.repositories.GetProfileRepository

class ProfileUseCase(
    private val getProfileRepository: GetProfileRepository
) {
    suspend fun getUserProfile(userId: String): User? {
        return BaseResponse.processResponse { getProfileRepository.getUser(userId) }
    }

    suspend fun getLocalUser(): User? {
        return BaseResponse.processResponse { getProfileRepository.getLocalUser() }
    }

    suspend fun logoutUser(): String? {
        return BaseResponse.processResponse { getProfileRepository.logoutUser() }
    }
}
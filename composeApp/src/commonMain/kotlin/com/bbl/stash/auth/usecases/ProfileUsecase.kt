package com.bbl.stash.auth.usecases

import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.User
import com.bbl.stash.auth.repositories.GetProfileRepository

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
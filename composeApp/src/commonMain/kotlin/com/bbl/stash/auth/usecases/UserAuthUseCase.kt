package com.bbl.stash.auth.usecases

import com.bbl.stash.auth.entity.AuthToken
import com.bbl.stash.auth.entity.BaseResponse
import com.bbl.stash.auth.entity.PasswordResetResponse
import com.bbl.stash.auth.entity.UserForgetPasswordBody
import com.bbl.stash.auth.entity.UserLoginBody
import com.bbl.stash.auth.entity.UserOTPVerifyBody
import com.bbl.stash.auth.entity.UserPasswordResetBody
import com.bbl.stash.auth.entity.UserRegisteredId
import com.bbl.stash.auth.entity.UserRegistrationBody
import com.bbl.stash.auth.repositories.UserAuthRepository

class UserAuthUseCase(
    private val userAuthRepository: UserAuthRepository
) {
    suspend fun registerUser(userRegistrationBody: UserRegistrationBody): UserRegisteredId? {
        return BaseResponse.processResponse { userAuthRepository.registerUser(userRegistrationBody) }
    }

    suspend fun loginUser(userLoginBody: UserLoginBody): AuthToken? {
        return BaseResponse.processResponse { userAuthRepository.loginUser(userLoginBody) }
    }

    suspend fun verifyOTP(userOTPVerifyBody: UserOTPVerifyBody, origin: String): AuthToken? {
        return BaseResponse.processResponse { userAuthRepository.verifyOTP(userOTPVerifyBody, origin) }
    }

    suspend fun forgetPasswordRequestOTP(userForgetPasswordBody: UserForgetPasswordBody): UserRegisteredId? {
       return BaseResponse.processResponse { userAuthRepository.forgetPasswordRequestOTP(userForgetPasswordBody) }
    }

    suspend fun resetPassword(userResetBody: UserPasswordResetBody, accessToken: String): PasswordResetResponse? {
        return BaseResponse.processResponse { userAuthRepository.resetPassword(userResetBody, accessToken) }
    }

    suspend fun getNewAccessToken(refreshToken: String): BaseResponse<AuthToken> {
        return userAuthRepository.getNewAccessToken(refreshToken)
    }
}
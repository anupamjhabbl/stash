package com.example.stash.auth.repositoryImpl

import com.example.stash.auth.clients.UserAuthClient
import com.example.stash.auth.entity.AuthToken
import com.example.stash.auth.entity.BaseResponse
import com.example.stash.auth.entity.PasswordResetResponse
import com.example.stash.auth.entity.UserForgetPasswordBody
import com.example.stash.auth.entity.UserLoginBody
import com.example.stash.auth.entity.UserOTPVerifyBody
import com.example.stash.auth.entity.UserPasswordResetBody
import com.example.stash.auth.entity.UserRegisteredId
import com.example.stash.auth.entity.UserRegistrationBody
import com.example.stash.auth.repositories.UserAuthRepository

class UserAuthNetwork(
    private val userAuthClient: UserAuthClient
): UserAuthRepository {
    override suspend fun registerUser(userRegistrationBody: UserRegistrationBody): BaseResponse<UserRegisteredId> {
        return userAuthClient.registerUser(userRegistrationBody)
    }

    override suspend fun loginUser(userLoginBody: UserLoginBody): BaseResponse<AuthToken> {
        return userAuthClient.loginUser(userLoginBody)
    }

    override suspend fun verifyOTP(userOTPVerifyBody: UserOTPVerifyBody, origin: String): BaseResponse<AuthToken> {
        return userAuthClient.verifyOTP(userOTPVerifyBody, origin)
    }

    override suspend fun forgetPasswordRequestOTP(userForgetPasswordBody: UserForgetPasswordBody): BaseResponse<UserRegisteredId> {
        return userAuthClient.forgetPasswordRequestOTP(userForgetPasswordBody)
    }

    override suspend fun resetPassword(userResetBody: UserPasswordResetBody, accessToken: String): BaseResponse<PasswordResetResponse> {
        return userAuthClient.resetPassword(userResetBody, accessToken)
    }

    override suspend fun getNewAccessToken(refreshToken: String): BaseResponse<AuthToken> {
        return userAuthClient.getNewAccessToken(refreshToken)
    }
}
package com.bbl.stash.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbl.stash.auth.entity.StashApplicationException
import com.bbl.stash.auth.entity.UserPasswordResetBody
import com.bbl.stash.auth.entity.UserResetPasswordFormState
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.Constants
import com.bbl.stash.common.RequestStatus
import com.bbl.stash.common.SafeIOUtil
import com.bbl.stash.common.StringUtils.isStrongPassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordResetVieModel(
    private val userAuthUseCase: UserAuthUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase
): ViewModel() {
    private val _state: MutableStateFlow<UserResetPasswordFormState> = MutableStateFlow(UserResetPasswordFormState())
    val state: StateFlow<UserResetPasswordFormState> = _state.asStateFlow()

    private val _userResetPasswordRequestStatus: MutableSharedFlow<RequestStatus<String>> = MutableSharedFlow()
    val userResetPasswordRequestStatus: SharedFlow<RequestStatus<String>> = _userResetPasswordRequestStatus.asSharedFlow()

    fun processEvent(viewEvent: UserAuthIntent.ResetPasswordAuth.ViewEvent) {
        when (viewEvent) {
            UserAuthIntent.ResetPasswordAuth.ViewEvent.ResetPassword -> resetPassword()
            is UserAuthIntent.ResetPasswordAuth.ViewEvent.UpdatePassword -> updatePassword(viewEvent.password)
            is UserAuthIntent.ResetPasswordAuth.ViewEvent.UpdateConfirmPassword -> updateConfirmPassword(viewEvent.password)
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            _userResetPasswordRequestStatus.emit(RequestStatus.Loading)
            val registerUserResult = SafeIOUtil.safeCall {
                userAuthUseCase.resetPassword(getResetPasswordBody(), "${Constants.HTTPHeaders.AUTHORIZATION_BEARER} ${authPreferencesUseCase.getAccessToken()}")
            }
            registerUserResult.onSuccess {
                _userResetPasswordRequestStatus.emit(RequestStatus.Success(""))
            }
            registerUserResult.onFailure { exception ->
                when (exception) {
                    is StashApplicationException -> {
                        _userResetPasswordRequestStatus.emit(RequestStatus.Error(exception.message))
                    }
                    is Exception -> {
                        _userResetPasswordRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                    }
                }
            }
        }
    }

    private fun getResetPasswordBody(): UserPasswordResetBody {
        return UserPasswordResetBody(
            password = state.value.password
        )
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordValid = confirmPassword == it.password
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordValid = password.isStrongPassword(),
                confirmPasswordValid = password == it.confirmPassword
            )
        }
    }
}
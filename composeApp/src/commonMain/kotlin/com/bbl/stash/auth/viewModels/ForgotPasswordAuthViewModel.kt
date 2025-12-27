package com.bbl.stash.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bbl.stash.auth.entity.StashApplicationException
import com.bbl.stash.auth.entity.UserForgetPasswordBody
import com.bbl.stash.auth.entity.UserForgetPasswordFormState
import com.bbl.stash.auth.entity.UserRegisteredId
import com.bbl.stash.auth.usecases.UserAuthUseCase
import com.bbl.stash.common.Constants
import com.bbl.stash.common.RequestStatus
import com.bbl.stash.common.SafeIOUtil
import com.bbl.stash.common.StringUtils.isValidEmail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordAuthViewModel(
    private val userAuthUseCase: UserAuthUseCase
): ViewModel() {
    private val _state: MutableStateFlow<UserForgetPasswordFormState> = MutableStateFlow(UserForgetPasswordFormState())
    val state: StateFlow<UserForgetPasswordFormState> = _state.asStateFlow()

    private val _userForgetPasswordRequestStatus: MutableSharedFlow<RequestStatus<UserRegisteredId>> = MutableSharedFlow()
    val userForgetPasswordRequestStatus: SharedFlow<RequestStatus<UserRegisteredId>> = _userForgetPasswordRequestStatus.asSharedFlow()

    fun processEvent(viewEvent: UserAuthIntent.ForgetPasswordAuth.ViewEvent) {
        when (viewEvent) {
            UserAuthIntent.ForgetPasswordAuth.ViewEvent.ResetPassword -> resetPassword()
            is UserAuthIntent.ForgetPasswordAuth.ViewEvent.UpdateEmail -> updateEmail(viewEvent.email)
        }
    }

    private fun getForgetPasswordRequestBody(): UserForgetPasswordBody {
        return UserForgetPasswordBody(
            email = state.value.email
        )
    }

    private fun  resetPassword() {
        viewModelScope.launch {
            _userForgetPasswordRequestStatus.emit(RequestStatus.Loading)
            val registerUserResult = SafeIOUtil.safeCall {
                userAuthUseCase.forgetPasswordRequestOTP(getForgetPasswordRequestBody())
            }
            registerUserResult.onSuccess { result ->
                result?.let {
                    _userForgetPasswordRequestStatus.emit(RequestStatus.Success(result))
                } ?: _userForgetPasswordRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            }
            registerUserResult.onFailure { exception ->
                when (exception) {
                    is StashApplicationException -> {
                        _userForgetPasswordRequestStatus.emit(RequestStatus.Error(exception.message))
                    }
                    is Exception -> {
                        _userForgetPasswordRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                    }
                }
            }
        }
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                isValid = email.isValidEmail()
            )
        }
    }
}
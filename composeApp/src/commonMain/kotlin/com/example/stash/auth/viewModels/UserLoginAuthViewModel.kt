package com.example.stash.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.auth.entity.AuthToken
import com.example.stash.auth.entity.StashApplicationException
import com.example.stash.auth.entity.UserLoginBody
import com.example.stash.auth.entity.UserLoginFormState
import com.example.stash.auth.usecases.AuthPreferencesUseCase
import com.example.stash.auth.usecases.ProfileUseCase
import com.example.stash.auth.usecases.UserAuthUseCase
import com.example.stash.common.Constants
import com.example.stash.common.RequestStatus
import com.example.stash.common.SafeIOUtil
import com.example.stash.common.StringUtils.isValidEmail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserLoginAuthViewModel(
    private val userAuthUseCase: UserAuthUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase,
    private val profileUseCase: ProfileUseCase
): ViewModel() {
    private val _state: MutableStateFlow<UserLoginFormState> = MutableStateFlow(UserLoginFormState())
    val state: StateFlow<UserLoginFormState> = _state.asStateFlow()

    private val _userLoginRequestStatus: MutableSharedFlow<RequestStatus<String>> = MutableSharedFlow()
    val userLoginRequestStatus: SharedFlow<RequestStatus<String>> = _userLoginRequestStatus.asSharedFlow()

    fun processEvent(viewEvent: UserAuthIntent.LoginAuth.ViewEvent) {
        when (viewEvent) {
            UserAuthIntent.LoginAuth.ViewEvent.LoginUser -> loginUser()
            is UserAuthIntent.LoginAuth.ViewEvent.UpdateEmail -> updateEmail(viewEvent.email)
            is UserAuthIntent.LoginAuth.ViewEvent.UpdatePassword -> updatePassword(viewEvent.password)
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            _userLoginRequestStatus.emit(RequestStatus.Loading)
            val registerUserResult = SafeIOUtil.safeCall {
                userAuthUseCase.loginUser(getLoginUserBody())
            }
            registerUserResult.onSuccess {  result ->
                result?.let {
                    saveTheToken(result)
                    fetchLocalUserData()
                } ?: _userLoginRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            }
            registerUserResult.onFailure { exception ->
                when (exception) {
                    is StashApplicationException -> {
                        _userLoginRequestStatus.emit(RequestStatus.Error(exception.message))
                    }
                    is Exception -> {
                        _userLoginRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                    }
                }
            }
        }
    }

    private suspend fun fetchLocalUserData() {
        val getUserDataRequest = SafeIOUtil.safeCall {
            profileUseCase.getLocalUser()
        }
        getUserDataRequest.onSuccess { user ->
            if (user == null) {
                removeToken()
                _userLoginRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
            } else {
                authPreferencesUseCase.saveLoggedUser(user)
                _userLoginRequestStatus.emit(RequestStatus.Success(""))
            }
        }
        getUserDataRequest.onFailure { exception ->
            removeToken()
            when (exception) {
                is StashApplicationException -> {
                    _userLoginRequestStatus.emit(RequestStatus.Error(exception.message))
                }
                is Exception -> {
                    _userLoginRequestStatus.emit(RequestStatus.Error(Constants.DEFAULT_ERROR))
                }
            }
        }
    }

    private fun removeToken() {
        authPreferencesUseCase.removeRefreshToken()
        authPreferencesUseCase.removeAccessToken()
    }

    private fun saveTheToken(authToken: AuthToken) {
        authPreferencesUseCase.saveAccessToken(authToken.accessToken)
        authPreferencesUseCase.saveRefreshToken(authToken.refreshToken)
    }

    private fun getLoginUserBody(): UserLoginBody {
        return UserLoginBody(
            email = state.value.email,
            password = state.value.password
        )
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                isValid = email.isValidEmail()
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password
            )
        }
    }
}
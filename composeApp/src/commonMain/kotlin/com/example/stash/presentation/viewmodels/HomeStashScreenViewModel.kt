package com.example.stash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.auth.entity.User
import com.example.stash.auth.usecases.AuthPreferencesUseCase
import com.example.stash.auth.usecases.ProfileUseCase
import com.example.stash.common.SafeIOUtil
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.usecase.StashDataUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeStashScreenViewModel(
    private val stashDataUseCase: StashDataUseCase,
    private val profileUseCase: ProfileUseCase,
    private val authPreferencesUseCase: AuthPreferencesUseCase
): ViewModel() {
    private val _stashScreenState: MutableStateFlow<HomeStashScreenState> = MutableStateFlow(HomeStashScreenState())
    val stashScreenState: StateFlow<HomeStashScreenState> = _stashScreenState.asStateFlow()

    private val _homeStashScreenEffect: MutableSharedFlow<HomeStashScreenEffect> = MutableSharedFlow()
    val homeStashScreenEffect: SharedFlow<HomeStashScreenEffect> = _homeStashScreenEffect.asSharedFlow()

    init {
        getStashData()
    }

    private fun getStashData() {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            stashDataUseCase.getCategoryDataWithItems(loggedUserId).onStart {
                    _stashScreenState.update {
                        it.copy(isLoading = true)
                    }
                }
                .catch {
                    _stashScreenState.update {
                        it.copy(isLoading = false)
                    }
                }
                .collect { list ->
                    _stashScreenState.update {
                        it.copy(
                            isLoading = false,
                            stashCategoryList = list
                        )
                    }
                }
        }
    }

    fun addCategoryItem(categoryName: String) {
        val loggedUserId = authPreferencesUseCase.getLoggedUserId() ?: return
        viewModelScope.launch {
            _stashScreenState.update { it.copy(isLoading = true) }
            try {
                stashDataUseCase.addStashCategory(categoryName, loggedUserId)
            } finally {
                _stashScreenState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun getLoggedUser(): User? {
        return authPreferencesUseCase.getLoggedUser()
    }

    fun logOutUser() {
        viewModelScope.launch {
            _stashScreenState.update { it.copy(isLoading = true) }
            val result = SafeIOUtil.safeCall {
                profileUseCase.logoutUser()
            }
            result.onSuccess {
                authPreferencesUseCase.removeUserData()
                _stashScreenState.update { it.copy(isLoading = false) }
                _homeStashScreenEffect.emit(HomeStashScreenEffect.LogOutUser)
            }
            result.onFailure {
                _stashScreenState.update { it.copy(isLoading = false) }
                _homeStashScreenEffect.emit(HomeStashScreenEffect.LogOutFailure)
            }
        }
    }
}

data class HomeStashScreenState(
    val isLoading: Boolean = true,
    val stashCategoryList: List<StashCategoryWithItem> = emptyList()
)

sealed interface HomeStashScreenEffect {
    data object LogOutUser: HomeStashScreenEffect
    data object LogOutFailure: HomeStashScreenEffect
}
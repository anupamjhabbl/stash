package com.example.stash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.domain.usecase.StashDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeStashScreenViewModel(
    private val stashDataUseCase: StashDataUseCase
): ViewModel() {
    private val _stashScreenState: MutableStateFlow<HomeStashScreenState> = MutableStateFlow(HomeStashScreenState())
    val stashScreenState: StateFlow<HomeStashScreenState> = _stashScreenState.asStateFlow()

    init {
        viewModelScope.launch {
            stashDataUseCase.getCategoryDataWithItems().onStart {
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
        viewModelScope.launch {
            _stashScreenState.update { it.copy(isLoading = true) }
            try {
                stashDataUseCase.addStashCategory(categoryName)
            } finally {
                _stashScreenState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class HomeStashScreenState(
    val isLoading: Boolean = false,
    val stashCategoryList: List<StashCategoryWithItem> = emptyList()
)
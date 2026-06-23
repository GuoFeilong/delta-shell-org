package com.delta.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.core.network.model.ApiResult
import com.delta.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            homeRepository.loadGreeting().collect { result ->
                _uiState.value = when (result) {
                    ApiResult.Loading -> HomeUiState.Loading
                    is ApiResult.Success -> HomeUiState.Ready(result.data.message)
                    is ApiResult.Error -> HomeUiState.Error(result.message)
                }
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Ready(val message: String) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

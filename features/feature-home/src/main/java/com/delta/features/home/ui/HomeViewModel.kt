package com.delta.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.features.home.data.repository.HomeRepository
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
            runCatching { homeRepository.loadFeed() }
                .onSuccess { feed ->
                    _uiState.value = HomeUiState.Ready("Home feed: ${feed.title}")
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Failed to load home feed")
                }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Ready(val message: String) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

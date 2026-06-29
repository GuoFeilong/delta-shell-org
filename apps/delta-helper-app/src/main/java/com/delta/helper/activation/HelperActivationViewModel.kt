package com.delta.helper.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.core.activation.model.ActivationEntrance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HelperActivationViewModel @Inject constructor(
    private val store: HelperActivationStatusStore,
    private val entranceSupport: ActivationEntranceSupport,
) : ViewModel() {
    val uiState: StateFlow<HelperActivationUiState> = store.uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HelperActivationUiState(),
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { store.refresh(force = true) }
    }

    suspend fun resolveEntrance(): ActivationEntrance = entranceSupport.resolveEntrance()
}

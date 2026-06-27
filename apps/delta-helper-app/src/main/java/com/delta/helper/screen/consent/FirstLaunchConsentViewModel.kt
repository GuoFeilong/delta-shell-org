package com.delta.helper.screen.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.helper.legal.LegalConsentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FirstLaunchConsentUiState(
    val isLoading: Boolean = true,
    val needsConsent: Boolean = false,
    val legalAccepted: Boolean = false,
    val serviceNatureAcknowledged: Boolean = false,
    val errorMessage: String? = null,
) {
    val canAccept: Boolean get() = legalAccepted && serviceNatureAcknowledged
}

@HiltViewModel
class FirstLaunchConsentViewModel @Inject constructor(
    private val legalConsentRepository: LegalConsentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FirstLaunchConsentUiState())
    val uiState: StateFlow<FirstLaunchConsentUiState> = _uiState.asStateFlow()

    init {
        refreshConsentState()
    }

    fun refreshConsentState() {
        viewModelScope.launch {
            val needsConsent = !legalConsentRepository.hasValidConsent()
            _uiState.update {
                it.copy(isLoading = false, needsConsent = needsConsent)
            }
        }
    }

    fun declineAndExit(onFinished: () -> Unit) {
        viewModelScope.launch {
            legalConsentRepository.clearConsent()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    needsConsent = true,
                    legalAccepted = false,
                    serviceNatureAcknowledged = false,
                    errorMessage = null,
                )
            }
            onFinished()
        }
    }

    fun onLegalCheckedChanged(checked: Boolean) {
        _uiState.update { it.copy(legalAccepted = checked, errorMessage = null) }
    }

    fun onServiceNatureAckChanged(checked: Boolean) {
        _uiState.update { it.copy(serviceNatureAcknowledged = checked, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun accept() {
        val state = _uiState.value
        if (!state.canAccept) {
            _uiState.update {
                it.copy(errorMessage = com.delta.helper.screen.legal.LegalCopy.FIRST_LAUNCH_VALIDATION_ERROR)
            }
            return
        }
        viewModelScope.launch {
            legalConsentRepository.acceptConsent()
            _uiState.update { it.copy(needsConsent = false, errorMessage = null) }
        }
    }
}

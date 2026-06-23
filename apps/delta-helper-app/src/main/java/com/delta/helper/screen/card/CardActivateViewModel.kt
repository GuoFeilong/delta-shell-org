package com.delta.helper.screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CardActivateViewModel @Inject constructor(
    private val cardActivationPort: CardActivationPort,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CardActivateUiState())
    val uiState: StateFlow<CardActivateUiState> = _uiState.asStateFlow()

    private val _openPurchaseUrl = MutableSharedFlow<String>()
    val openPurchaseUrl = _openPurchaseUrl.asSharedFlow()

    init {
        loadPurchaseUrl()
    }

    fun onCardCodeChanged(value: String) {
        _uiState.update { it.copy(cardCode = value, errorMessage = null) }
    }

    fun onLegalCheckedChanged(checked: Boolean) {
        _uiState.update { it.copy(legalAccepted = checked, errorMessage = null) }
    }

    fun activate() {
        val state = _uiState.value
        if (!state.legalAccepted) {
            _uiState.update { it.copy(errorMessage = "请先阅读并同意相关协议") }
            return
        }
        val code = state.cardCode.trim()
        if (code.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入激活码") }
            return
        }
        if (state.isActivating) return
        _uiState.update { it.copy(showActivateConfirm = true) }
    }

    fun dismissActivateConfirm() {
        _uiState.update { it.copy(showActivateConfirm = false) }
    }

    fun confirmActivate() {
        val code = _uiState.value.cardCode.trim()
        _uiState.update { it.copy(showActivateConfirm = false, isActivating = true, errorMessage = null) }
        viewModelScope.launch {
            when (val outcome = cardActivationPort.redeemCard(code)) {
                is CardActivationOutcome.Success -> {
                    _uiState.update {
                        it.copy(
                            isActivating = false,
                            isActivated = true,
                            successMessage = outcome.message,
                        )
                    }
                }
                is CardActivationOutcome.Failure -> {
                    _uiState.update {
                        it.copy(
                            isActivating = false,
                            errorMessage = outcome.message,
                        )
                    }
                }
            }
        }
    }

    fun purchaseCard() {
        val url = _uiState.value.purchaseUrl?.trim().orEmpty()
        if (url.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "暂无购卡链接，请联系购买渠道") }
            return
        }
        _uiState.update { it.copy(purchaseConfirmUrl = url) }
    }

    fun dismissPurchaseConfirm() {
        _uiState.update { it.copy(purchaseConfirmUrl = null) }
    }

    fun confirmPurchase() {
        val url = _uiState.value.purchaseConfirmUrl ?: return
        _uiState.update { it.copy(purchaseConfirmUrl = null) }
        viewModelScope.launch { _openPurchaseUrl.emit(url) }
    }

    private fun loadPurchaseUrl() {
        viewModelScope.launch {
            val url = cardActivationPort.fetchPurchaseUrl()
            if (url == null) {
                _uiState.update { it.copy(purchaseUrl = null, isActivated = true) }
            } else {
                _uiState.update { it.copy(purchaseUrl = url) }
            }
        }
    }
}

data class CardActivateUiState(
    val cardCode: String = "",
    val legalAccepted: Boolean = false,
    val purchaseUrl: String? = null,
    val isActivating: Boolean = false,
    val isActivated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val purchaseConfirmUrl: String? = null,
    val showActivateConfirm: Boolean = false,
) {
    val canActivate: Boolean
        get() = legalAccepted && cardCode.isNotBlank() && !isActivating
}

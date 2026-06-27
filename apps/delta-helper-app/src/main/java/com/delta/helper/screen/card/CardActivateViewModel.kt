package com.delta.helper.screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.helper.activation.ActivationPlanDisplayFormatter
import com.delta.helper.activation.DeviceProfileSynchronizer
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
    private val deviceProfileSynchronizer: DeviceProfileSynchronizer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CardActivateUiState())
    val uiState: StateFlow<CardActivateUiState> = _uiState.asStateFlow()

    private val _openPurchaseUrl = MutableSharedFlow<String>()
    val openPurchaseUrl = _openPurchaseUrl.asSharedFlow()

    init {
        deviceProfileSynchronizer.scheduleSync()
        loadPurchaseOptions()
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun onCardCodeChanged(value: String) {
        _uiState.update { it.copy(cardCode = value, errorMessage = null) }
    }

    fun onLegalCheckedChanged(checked: Boolean) {
        _uiState.update { it.copy(legalAccepted = checked, errorMessage = null) }
    }

    fun onServiceNatureAckChanged(checked: Boolean) {
        _uiState.update { it.copy(serviceNatureAcknowledged = checked, errorMessage = null) }
    }

    fun onPlanSelected(index: Int) {
        _uiState.update { it.copy(selectedPlanIndex = index, errorMessage = null) }
    }

    fun onAccessModeChanged(mode: CardActivateAccessMode) {
        _uiState.update { it.copy(accessMode = mode, errorMessage = null) }
    }

    fun onTipsExpandedChanged(expanded: Boolean) {
        _uiState.update { it.copy(tipsExpanded = expanded) }
    }

    fun activate() {
        val state = _uiState.value
        if (!state.legalAccepted || !state.serviceNatureAcknowledged) {
            _uiState.update {
                it.copy(errorMessage = "请先阅读并同意相关协议，并确认已理解服务性质")
            }
            return
        }
        val code = state.cardCode.trim()
        if (code.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入内容访问码") }
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
                            activationSuccessToken = it.activationSuccessToken + 1,
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
        val url = _uiState.value.selectedPurchaseUrl?.trim().orEmpty()
        if (url.isEmpty()) {
            _uiState.update { it.copy(errorMessage = CardActivateCopy.NO_PURCHASE_LINK) }
            return
        }
        _uiState.update { it.copy(purchaseConfirmUrl = url) }
    }

    fun dismissPurchaseConfirm() {
        _uiState.update { it.copy(purchaseConfirmUrl = null) }
    }

    fun confirmPurchase() {
        val url = _uiState.value.purchaseConfirmUrl ?: return
        _uiState.update {
            it.copy(
                purchaseConfirmUrl = null,
                accessMode = CardActivateAccessMode.ACTIVATE,
            )
        }
        viewModelScope.launch { _openPurchaseUrl.emit(url) }
    }

    private fun loadPurchaseOptions() {
        viewModelScope.launch {
            val options = cardActivationPort.fetchPurchaseOptions()
            if (options.isEmpty()) {
                val fallbackUrl = cardActivationPort.fetchPurchaseUrl()
                if (fallbackUrl == null) {
                    _uiState.update {
                        it.copy(
                            purchaseOptions = emptyList(),
                            accessMode = CardActivateAccessMode.ACTIVATE,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            purchaseOptions = listOf(
                                CardPurchaseOptionUi(
                                    planCode = "LIFETIME",
                                    label = "永久",
                                    priceDisplay = null,
                                    originalPriceDisplay = null,
                                    savingsDisplay = null,
                                    purchaseUrl = fallbackUrl,
                                    default = true,
                                ),
                            ),
                            selectedPlanIndex = 0,
                        )
                    }
                }
                return@launch
            }
            val defaultIndex = options.indexOfFirst { it.default }.let { if (it >= 0) it else 0 }
            _uiState.update {
                it.copy(
                    purchaseOptions = options.map { option ->
                        CardPurchaseOptionUi(
                            planCode = option.planCode,
                            label = option.label,
                            priceDisplay = option.priceDisplay,
                            originalPriceDisplay = option.originalPriceDisplay,
                            savingsDisplay = option.savingsDisplay,
                            purchaseUrl = option.purchaseUrl,
                            default = option.default,
                        )
                    },
                    selectedPlanIndex = defaultIndex,
                    accessMode = CardActivateAccessMode.PURCHASE,
                )
            }
        }
    }
}

data class CardPurchaseOptionUi(
    val planCode: String,
    val label: String,
    val priceDisplay: String?,
    val originalPriceDisplay: String? = null,
    val savingsDisplay: String? = null,
    val purchaseUrl: String,
    val default: Boolean,
)

data class CardActivateUiState(
    val cardCode: String = "",
    val legalAccepted: Boolean = false,
    val serviceNatureAcknowledged: Boolean = false,
    val purchaseOptions: List<CardPurchaseOptionUi> = emptyList(),
    val selectedPlanIndex: Int = 0,
    val accessMode: CardActivateAccessMode = CardActivateAccessMode.PURCHASE,
    val tipsExpanded: Boolean = false,
    val isActivating: Boolean = false,
    val isActivated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val purchaseConfirmUrl: String? = null,
    val showActivateConfirm: Boolean = false,
    val activationSuccessToken: Long = 0L,
) {
    val hasPurchaseOptions: Boolean
        get() = purchaseOptions.isNotEmpty()

    val selectedPurchaseUrl: String?
        get() = purchaseOptions.getOrNull(selectedPlanIndex)?.purchaseUrl

    val selectedPlanTitle: String?
        get() = purchaseOptions.getOrNull(selectedPlanIndex)?.let {
            ActivationPlanDisplayFormatter.format(it).title
        }

    val selectedPlanHint: String?
        get() {
            val display = purchaseOptions.getOrNull(selectedPlanIndex)?.let {
                ActivationPlanDisplayFormatter.format(it)
            } ?: return null
            return buildString {
                append("已选：${display.title}")
                display.priceDisplay?.let { append(" · $it") }
                display.savingsDisplay?.let { append("（$it）") }
            }
        }

    val purchaseButtonText: String
        get() {
            val display = purchaseOptions.getOrNull(selectedPlanIndex)?.let {
                ActivationPlanDisplayFormatter.format(it)
            } ?: return CardActivateCopy.PURCHASE_BUTTON
            return when {
                !display.priceDisplay.isNullOrBlank() -> "${display.priceDisplay} ${CardActivateCopy.PURCHASE_BUTTON}"
                else -> "购买${display.title}访问码"
            }
        }

    val canPurchase: Boolean
        get() = !selectedPurchaseUrl.isNullOrBlank() && !isActivating

    val canActivate: Boolean
        get() = legalAccepted &&
            serviceNatureAcknowledged &&
            cardCode.isNotBlank() &&
            !isActivating
}

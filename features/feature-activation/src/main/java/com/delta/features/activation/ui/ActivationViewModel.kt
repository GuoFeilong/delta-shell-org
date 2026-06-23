package com.delta.features.activation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.core.activation.model.ActivationDeviceStatus
import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.model.ReleaseGate
import com.delta.core.activation.model.UiMode
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.activation.repository.ReleaseGateRepository
import com.delta.core.network.model.ApiResult
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
class ActivationViewModel @Inject constructor(
    private val activationRepository: ActivationRepository,
    private val releaseGateRepository: ReleaseGateRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState.asStateFlow()

    private val _openPurchaseUrl = MutableSharedFlow<String>()
    val openPurchaseUrl = _openPurchaseUrl.asSharedFlow()

    init {
        evaluateReleaseGateThenStatus()
    }

    fun onCardCodeChanged(value: String) {
        _uiState.update { it.copy(cardCode = value, errorMessage = null) }
    }

    fun onTabSelected(tab: ActivationTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
    }

    fun markActivated() {
        _uiState.update {
            it.copy(
                isChecking = false,
                isBusy = false,
                isActivated = true,
                statusMessage = "已激活，可以正常使用",
                errorMessage = null,
            )
        }
    }

    fun evaluateReleaseGateThenStatus() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isChecking = true, isBusy = true, errorMessage = null)
            }
            releaseGateRepository.evaluate().collect { gateResult ->
                when (gateResult) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> {
                        val gate = gateResult.data
                        _uiState.update { it.copy(releaseGate = gate) }
                        if (gate.uiMode == UiMode.TOOL) {
                            _uiState.update {
                                it.copy(
                                    isChecking = false,
                                    isBusy = false,
                                    isActivated = true,
                                    statusMessage = "审核模式（无需激活）",
                                )
                            }
                        } else {
                            refreshActivationStatus()
                        }
                    }
                    is ApiResult.Error -> refreshActivationStatus()
                }
            }
        }
    }

    fun refreshStatus() {
        evaluateReleaseGateThenStatus()
    }

    private fun refreshActivationStatus() {
        viewModelScope.launch {
            activationRepository.getActivationStatus().collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> {
                        val activated = result.data.activated &&
                            result.data.status == ActivationDeviceStatus.ACTIVE
                        _uiState.update {
                            it.copy(
                                isChecking = false,
                                isBusy = false,
                                isActivated = activated,
                                statusMessage = statusMessageFor(result.data.status, activated),
                                canRetry = false,
                                errorMessage = null,
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isChecking = false,
                                isBusy = false,
                                canRetry = true,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun redeemCard() {
        val cardCode = _uiState.value.cardCode.trim()
        if (cardCode.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = ActivationErrorCodes.messageFor(
                        ActivationErrorCodes.CARD_CODE_REQUIRED,
                        "卡密不能为空",
                    ),
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            activationRepository.redeemCard(cardCode).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> {
                        val activated = result.data.activated &&
                            result.data.status == ActivationDeviceStatus.ACTIVE
                        _uiState.update {
                            it.copy(
                                isBusy = false,
                                isActivated = activated,
                                statusMessage = statusMessageFor(result.data.status, activated),
                                errorMessage = if (activated) null else "激活未完成，请检查卡密",
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isBusy = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun openPurchaseUrl() {
        val cached = _uiState.value.purchaseUrl
        if (!cached.isNullOrBlank()) {
            viewModelScope.launch { _openPurchaseUrl.emit(cached) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, errorMessage = null) }
            activationRepository.getPurchaseUrl().collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> {
                        val url = result.data.purchaseUrl
                        _uiState.update {
                            it.copy(
                                isBusy = false,
                                purchaseUrl = url,
                                errorMessage = if (url.isNullOrBlank()) "暂无购卡链接" else null,
                            )
                        }
                        if (!url.isNullOrBlank()) {
                            _openPurchaseUrl.emit(url)
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isBusy = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun statusMessageFor(
        status: ActivationDeviceStatus,
        activated: Boolean,
    ): String = when {
        activated -> "已激活，可以正常使用"
        status == ActivationDeviceStatus.REVOKED -> "激活已被撤销，请重新激活"
        else -> "请选择卡密激活或完成任务激活"
    }
}

enum class ActivationTab {
    CARD,
    TASK,
}

data class ActivationUiState(
    val isChecking: Boolean = true,
    val isBusy: Boolean = false,
    val isActivated: Boolean = false,
    val cardCode: String = "",
    val purchaseUrl: String? = null,
    val statusMessage: String = "正在检查激活状态…",
    val errorMessage: String? = null,
    val canRetry: Boolean = false,
    val releaseGate: ReleaseGate? = null,
    val selectedTab: ActivationTab = ActivationTab.CARD,
) {
    val canRedeem: Boolean
        get() = !isBusy && !isActivated && cardCode.isNotBlank()

    val canOpenPurchaseUrl: Boolean
        get() = !isBusy && !isActivated
}

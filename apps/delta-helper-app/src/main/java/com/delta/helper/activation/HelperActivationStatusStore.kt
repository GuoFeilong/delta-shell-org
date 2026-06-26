package com.delta.helper.activation

import com.delta.core.activation.model.ActivationDeviceStatus
import com.delta.core.activation.model.ActivationStatus
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.activation.repository.awaitActivationStatus
import com.delta.core.network.model.ApiResult
import com.delta.helper.BuildConfig
import com.delta.helper.screen.card.ActivationCheckResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HelperActivationUiState(
    val gateEnabled: Boolean = true,
    val loading: Boolean = false,
    val activated: Boolean = false,
    val statusSummary: String = "",
    val accessLine: String = "",
    val planCode: String? = null,
    val bannerSourceLine: String = "",
    val bannerTitle: String = "尚未开通",
    val bannerSubtitle: String = "购买访问码（月/季/年/永久）开通；到期需续费，无自动扣费",
    val showActivateAction: Boolean = true,
)

@Singleton
class HelperActivationStatusStore @Inject constructor(
    private val activationRepository: ActivationRepository,
    private val activationGateSupport: ActivationGateSupport,
    private val localSession: LocalActivationSession,
) {
    private val _uiState = MutableStateFlow(HelperActivationUiState())
    val uiState: StateFlow<HelperActivationUiState> = _uiState.asStateFlow()

    suspend fun refresh(force: Boolean = false) {
        if (_uiState.value.loading && !force) return
        _uiState.update { it.copy(loading = true) }
        try {
            applyResolvedStatus(resolveStatus())
        } finally {
            _uiState.update { it.copy(loading = false) }
        }
    }

    suspend fun checkActivationForLaunch(): ActivationCheckResult {
        refresh(force = true)
        val state = _uiState.value
        return ActivationCheckResult(
            activated = state.activated,
            message = if (state.activated) ActivationMessages.ACTIVATED else null,
            statusSummary = state.statusSummary.takeIf { it.isNotEmpty() },
            accessLine = state.accessLine.takeIf { it.isNotEmpty() },
        )
    }

    private suspend fun resolveStatus(): ResolvedActivationStatus {
        if (BuildConfig.MOCK_ALREADY_ACTIVATED) {
            return ResolvedActivationStatus(
                gateEnabled = true,
                activated = true,
                status = null,
            )
        }
        if (!activationGateSupport.isGateEnabled()) {
            return ResolvedActivationStatus(
                gateEnabled = false,
                activated = true,
                status = null,
            )
        }
        if (BuildConfig.SIMULATE_NOT_ACTIVATED) {
            if (localSession.isLocallyActivated()) {
                return ResolvedActivationStatus(
                    gateEnabled = true,
                    activated = true,
                    status = null,
                )
            }
            return ResolvedActivationStatus(
                gateEnabled = true,
                activated = false,
                status = null,
            )
        }
        return when (val result = activationRepository.awaitActivationStatus()) {
            is ApiResult.Success -> {
                val status = result.data
                val activated = status.activated && status.status == ActivationDeviceStatus.ACTIVE
                ResolvedActivationStatus(
                    gateEnabled = true,
                    activated = activated,
                    status = status.takeIf { activated },
                )
            }
            is ApiResult.Error -> ResolvedActivationStatus(
                gateEnabled = true,
                activated = false,
                status = null,
            )
            ApiResult.Loading -> ResolvedActivationStatus(
                gateEnabled = true,
                activated = false,
                status = null,
            )
        }
    }

    private fun applyResolvedStatus(resolved: ResolvedActivationStatus) {
        if (!resolved.gateEnabled) {
            _uiState.value = HelperActivationUiState(
                gateEnabled = false,
                activated = true,
                bannerTitle = "已开通",
                bannerSubtitle = "本渠道无需单独激活",
                showActivateAction = false,
            )
            return
        }
        if (!resolved.activated) {
            _uiState.value = HelperActivationUiState(
                gateEnabled = true,
                activated = false,
                bannerTitle = "尚未开通",
                bannerSubtitle = "购买访问码（月/季/年/永久）开通；到期需续费，无自动扣费",
                showActivateAction = true,
            )
            return
        }
        val status = resolved.status
        val summary = status?.let(ActivationAccessFormatter::formatStatusSummary).orEmpty()
        val accessLine = status?.let(ActivationAccessFormatter::formatAccessExpiry).orEmpty()
        val sourceLine = status?.let { ActivationAccessFormatter.formatSource(it.source) }.orEmpty()
        _uiState.value = HelperActivationUiState(
            gateEnabled = true,
            activated = true,
            statusSummary = summary,
            accessLine = accessLine,
            planCode = status?.planCode,
            bannerSourceLine = sourceLine,
            bannerTitle = "已开通",
            bannerSubtitle = summary.ifBlank { "内容访问已生效" },
            showActivateAction = true,
        )
    }

    private data class ResolvedActivationStatus(
        val gateEnabled: Boolean,
        val activated: Boolean,
        val status: ActivationStatus?,
    )
}

package com.delta.helper.screen.card

import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.network.model.ApiResult
import com.delta.helper.BuildConfig
import com.delta.helper.activation.ActivationAccessFormatter
import com.delta.helper.activation.ActivationGateSupport
import com.delta.helper.activation.ActivationMessages
import com.delta.helper.activation.LocalActivationSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class RemoteCardActivationPort @Inject constructor(
    private val activationRepository: ActivationRepository,
    private val localSession: LocalActivationSession,
    private val activationGateSupport: ActivationGateSupport,
) : CardActivationPort {
    override suspend fun fetchPurchaseOptions(): List<CardPurchaseOption> =
        when (val result = activationRepository.getPurchaseOptions().first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> result.data.options.map { option ->
                CardPurchaseOption(
                    planCode = option.planCode,
                    label = option.label,
                    priceDisplay = option.priceDisplay,
                    originalPriceDisplay = option.originalPriceDisplay,
                    savingsDisplay = option.savingsDisplay,
                    purchaseUrl = option.purchaseUrl,
                    default = option.default,
                )
            }
            else -> emptyList()
        }

    override suspend fun fetchPurchaseUrl(): String? {
        val options = fetchPurchaseOptions()
        if (options.isNotEmpty()) {
            return options.firstOrNull { it.default }?.purchaseUrl?.trim()?.takeIf { it.isNotEmpty() }
                ?: options.firstOrNull()?.purchaseUrl?.trim()?.takeIf { it.isNotEmpty() }
        }
        return when (val result = activationRepository.getPurchaseUrl().first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> result.data.purchaseUrl?.trim()?.takeIf { it.isNotEmpty() }
            else -> null
        }
    }

    override suspend fun isActivationGateEnabled(): Boolean = activationGateSupport.isGateEnabled()

    override suspend fun redeemCard(cardCode: String): CardActivationOutcome =
        when (val result = activationRepository.redeemCard(cardCode).first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> {
                if (result.data.activated) {
                    if (BuildConfig.SIMULATE_NOT_ACTIVATED) {
                        localSession.markLocallyActivated()
                    }
                    val expiry = ActivationAccessFormatter.formatAccessExpiry(result.data)
                    val message = if (expiry.isNotBlank()) {
                        "${ActivationMessages.ACTIVATED} · $expiry"
                    } else {
                        ActivationMessages.ACTIVATED
                    }
                    CardActivationOutcome.Success(message = message)
                } else {
                    CardActivationOutcome.Failure(message = "激活失败，请检查激活码或联系客服")
                }
            }
            is ApiResult.Error -> CardActivationOutcome.Failure(
                message = ActivationErrorCodes.messageFor(result.code, result.message),
                code = result.code,
            )
            ApiResult.Loading -> CardActivationOutcome.Failure(message = "激活失败，请重试")
        }

    companion object {
        const val ACTIVATED_MESSAGE = ActivationMessages.ACTIVATED
    }
}

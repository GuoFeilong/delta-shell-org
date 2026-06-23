package com.delta.helper.screen.card

import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.network.model.ApiResult
import com.delta.helper.BuildConfig
import com.delta.helper.activation.LocalActivationSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class RemoteCardActivationPort @Inject constructor(
    private val activationRepository: ActivationRepository,
    private val localSession: LocalActivationSession,
) : CardActivationPort {
    override suspend fun fetchPurchaseUrl(): String? =
        when (val result = activationRepository.getPurchaseUrl().first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> result.data.purchaseUrl?.trim()?.takeIf { it.isNotEmpty() }
            else -> null
        }

    override suspend fun isActivationGateEnabled(): Boolean = fetchPurchaseUrl() != null

    override suspend fun redeemCard(cardCode: String): CardActivationOutcome =
        when (val result = activationRepository.redeemCard(cardCode).first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> {
                if (result.data.activated) {
                    if (BuildConfig.SIMULATE_NOT_ACTIVATED) {
                        localSession.markLocallyActivated()
                    }
                    CardActivationOutcome.Success(message = ACTIVATED_MESSAGE)
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
        const val ACTIVATED_MESSAGE = "内容访问已开通"
    }
}

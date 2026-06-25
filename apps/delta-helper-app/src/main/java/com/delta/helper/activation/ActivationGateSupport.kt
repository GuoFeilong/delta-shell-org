package com.delta.helper.activation

import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.network.model.ApiResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class ActivationGateSupport @Inject constructor(
    private val activationRepository: ActivationRepository,
) {
    suspend fun isGateEnabled(): Boolean {
        val options = when (
            val result = activationRepository.getPurchaseOptions().first { it !is ApiResult.Loading }
        ) {
            is ApiResult.Success -> result.data.options
            else -> emptyList()
        }
        if (options.isNotEmpty()) return true

        val fallbackUrl = when (
            val result = activationRepository.getPurchaseUrl().first { it !is ApiResult.Loading }
        ) {
            is ApiResult.Success -> result.data.purchaseUrl?.trim()?.takeIf { it.isNotEmpty() }
            else -> null
        }
        return fallbackUrl != null
    }
}

object ActivationMessages {
    const val ACTIVATED = "内容访问已开通"
}

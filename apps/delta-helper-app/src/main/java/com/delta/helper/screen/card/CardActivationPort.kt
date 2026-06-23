package com.delta.helper.screen.card

/**
 * 卡密激活接口预留层。
 *
 * 后续接入 [com.delta.core.activation.repository.ActivationRepository] 时，
 * 在此模块提供 App 层实现并替换 [PlaceholderCardActivationPort]。
 */
interface CardActivationPort {
    suspend fun fetchPurchaseUrl(): String?

    suspend fun redeemCard(cardCode: String): CardActivationOutcome
}

sealed interface CardActivationOutcome {
    data class Success(val message: String) : CardActivationOutcome

    data class Failure(val message: String, val code: Int? = null) : CardActivationOutcome
}

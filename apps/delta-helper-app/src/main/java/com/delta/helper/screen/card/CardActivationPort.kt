package com.delta.helper.screen.card

/**
 * 卡密激活接口预留层。
 *
 * 后续接入 [com.delta.core.activation.repository.ActivationRepository] 时，
 * 在此模块提供 App 层实现并替换 [PlaceholderCardActivationPort]。
 */
interface CardActivationPort {
    suspend fun fetchPurchaseOptions(): List<CardPurchaseOption>

    suspend fun fetchPurchaseUrl(): String?

    /** 后端未下发购卡链接时视为关闭激活门禁，App 可直接使用。 */
    suspend fun isActivationGateEnabled(): Boolean

    suspend fun redeemCard(cardCode: String): CardActivationOutcome
}

data class CardPurchaseOption(
    val planCode: String,
    val label: String,
    val priceDisplay: String?,
    val originalPriceDisplay: String? = null,
    val savingsDisplay: String? = null,
    val purchaseUrl: String,
    val default: Boolean,
)

sealed interface CardActivationOutcome {
    data class Success(val message: String) : CardActivationOutcome

    data class Failure(val message: String, val code: Int? = null) : CardActivationOutcome
}

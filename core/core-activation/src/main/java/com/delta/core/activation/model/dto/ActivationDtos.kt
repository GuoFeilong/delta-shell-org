package com.delta.core.activation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivationStatusDto(
    val activated: Boolean,
    val status: String,
    val source: String? = null,
    val publisherKey: String? = null,
    val clientOs: String? = null,
    val clientChannel: String? = null,
    val wechatOpenid: String? = null,
    val activatedAt: String? = null,
    val appPackageName: String? = null,
    val planCode: String? = null,
    val accessExpiresAt: String? = null,
    val daysRemaining: Long? = null,
)

@Serializable
data class CardPurchaseUrlDto(
    val purchaseUrl: String? = null,
)

@Serializable
data class CardPurchaseOptionDto(
    val planCode: String,
    val label: String,
    val priceDisplay: String? = null,
    val purchaseUrl: String,
    val default: Boolean = false,
)

@Serializable
data class CardPurchaseOptionsDto(
    val options: List<CardPurchaseOptionDto> = emptyList(),
)

@Serializable
data class CardRedeemRequestDto(
    val cardCode: String,
    val appPackageName: String? = null,
)

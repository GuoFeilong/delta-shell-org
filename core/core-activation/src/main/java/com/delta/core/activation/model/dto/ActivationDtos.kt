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
)

@Serializable
data class CardPurchaseUrlDto(
    val purchaseUrl: String? = null,
)

@Serializable
data class CardRedeemRequestDto(
    val cardCode: String,
    val appPackageName: String? = null,
)

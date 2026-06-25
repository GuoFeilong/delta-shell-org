package com.delta.core.activation.model

import com.delta.core.activation.model.dto.ActivationStatusDto
import com.delta.core.activation.model.dto.CardPurchaseOptionDto
import com.delta.core.activation.model.dto.CardPurchaseOptionsDto
import com.delta.core.activation.model.dto.CardPurchaseUrlDto

fun ActivationStatusDto.toDomain(): ActivationStatus = ActivationStatus(
    activated = activated,
    status = ActivationDeviceStatus.from(status),
    source = ActivationSource.from(source),
    publisherKey = publisherKey,
    clientOs = clientOs,
    clientChannel = clientChannel,
    activatedAt = activatedAt,
    appPackageName = appPackageName,
    planCode = planCode,
    accessExpiresAt = accessExpiresAt,
    daysRemaining = daysRemaining,
)

fun CardPurchaseUrlDto.toDomain(): CardPurchaseUrl = CardPurchaseUrl(
    purchaseUrl = purchaseUrl,
)

fun CardPurchaseOptionsDto.toDomain(): CardPurchaseOptions = CardPurchaseOptions(
    options = options.map { it.toDomain() },
)

fun CardPurchaseOptionDto.toDomain(): CardPurchaseOption = CardPurchaseOption(
    planCode = planCode,
    label = label,
    priceDisplay = priceDisplay,
    purchaseUrl = purchaseUrl,
    default = default,
)

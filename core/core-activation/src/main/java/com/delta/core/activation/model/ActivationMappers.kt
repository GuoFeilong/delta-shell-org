package com.delta.core.activation.model

import com.delta.core.activation.model.dto.ActivationStatusDto
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
)

fun CardPurchaseUrlDto.toDomain(): CardPurchaseUrl = CardPurchaseUrl(
    purchaseUrl = purchaseUrl,
)

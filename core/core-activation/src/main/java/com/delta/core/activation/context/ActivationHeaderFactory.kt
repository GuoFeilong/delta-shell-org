package com.delta.core.activation.context

import com.delta.core.activation.device.DeviceIdProvider

data class ActivationClientHeaders(
    val deviceId: String,
    val clientOs: String,
    val clientChannel: String,
    val clientVersion: String?,
    val publisherKey: String?,
)

class ActivationHeaderFactory(
    private val clientContext: ActivationClientContext,
    private val deviceIdProvider: DeviceIdProvider,
) {
    suspend fun build(): ActivationClientHeaders = ActivationClientHeaders(
        deviceId = deviceIdProvider.getDeviceId(),
        clientOs = clientContext.clientOs,
        clientChannel = clientContext.clientChannel,
        clientVersion = clientContext.clientVersionCode?.toString(),
        publisherKey = clientContext.publisherKey,
    )
}

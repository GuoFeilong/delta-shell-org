package com.delta.core.activation.context

import com.delta.core.activation.device.DeviceIdProvider

data class ActivationClientHeaders(
    val deviceId: String,
    val clientOs: String,
    val clientChannel: String,
    val clientVersion: String?,
    val publisherKey: String?,
    val deviceModel: String? = null,
    val deviceBrand: String? = null,
    val deviceManufacturer: String? = null,
    val osVersion: String? = null,
    val osSdkInt: String? = null,
    val clientVersionName: String? = null,
    val locale: String? = null,
)

class ActivationHeaderFactory(
    private val clientContext: ActivationClientContext,
    private val deviceIdProvider: DeviceIdProvider,
    private val deviceProfileProvider: DeviceProfileProvider,
) {
    suspend fun build(): ActivationClientHeaders {
        val profile = deviceProfileProvider.snapshot()
        return ActivationClientHeaders(
            deviceId = deviceIdProvider.getDeviceId(),
            clientOs = clientContext.clientOs,
            clientChannel = clientContext.clientChannel,
            clientVersion = clientContext.clientVersionCode?.toString(),
            publisherKey = clientContext.publisherKey,
            deviceModel = profile.deviceModel,
            deviceBrand = profile.deviceBrand,
            deviceManufacturer = profile.deviceManufacturer,
            osVersion = profile.osVersion,
            osSdkInt = profile.osSdkInt,
            clientVersionName = clientContext.clientVersionName,
            locale = profile.locale,
        )
    }
}

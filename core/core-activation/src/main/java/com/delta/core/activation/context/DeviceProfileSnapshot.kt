package com.delta.core.activation.context

data class DeviceProfileSnapshot(
    val deviceModel: String?,
    val deviceBrand: String?,
    val deviceManufacturer: String?,
    val osVersion: String?,
    val osSdkInt: String?,
    val locale: String?,
)

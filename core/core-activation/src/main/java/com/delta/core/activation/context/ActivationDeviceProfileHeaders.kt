package com.delta.core.activation.context

object ActivationDeviceProfileHeaders {
    const val DEVICE_MODEL = "X-Device-Model"
    const val DEVICE_BRAND = "X-Device-Brand"
    const val DEVICE_MANUFACTURER = "X-Device-Manufacturer"
    const val OS_VERSION = "X-Os-Version"
    const val OS_SDK_INT = "X-Os-Sdk-Int"
    const val CLIENT_VERSION_NAME = "X-Client-Version-Name"
    const val DEVICE_LOCALE = "X-Device-Locale"
}

fun ActivationClientHeaders.toDeviceProfileHeaderMap(): Map<String, String> = buildMap {
    deviceModel?.let { put(ActivationDeviceProfileHeaders.DEVICE_MODEL, it) }
    deviceBrand?.let { put(ActivationDeviceProfileHeaders.DEVICE_BRAND, it) }
    deviceManufacturer?.let { put(ActivationDeviceProfileHeaders.DEVICE_MANUFACTURER, it) }
    osVersion?.let { put(ActivationDeviceProfileHeaders.OS_VERSION, it) }
    osSdkInt?.let { put(ActivationDeviceProfileHeaders.OS_SDK_INT, it) }
    clientVersionName?.let { put(ActivationDeviceProfileHeaders.CLIENT_VERSION_NAME, it) }
    locale?.let { put(ActivationDeviceProfileHeaders.DEVICE_LOCALE, it) }
}

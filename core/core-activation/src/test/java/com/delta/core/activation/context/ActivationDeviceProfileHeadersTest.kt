package com.delta.core.activation.context

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivationDeviceProfileHeadersTest {

    @Test
    fun `toDeviceProfileHeaderMap includes only non-null fields`() {
        val map = ActivationClientHeaders(
            deviceId = "dev-1",
            clientOs = "ANDROID",
            clientChannel = "official",
            clientVersion = "100",
            publisherKey = "official",
            deviceModel = "Pixel 7",
            deviceBrand = "google",
            osVersion = "14",
            osSdkInt = "34",
            clientVersionName = "1.0.0",
            locale = "zh_CN",
        ).toDeviceProfileHeaderMap()

        assertEquals("Pixel 7", map[ActivationDeviceProfileHeaders.DEVICE_MODEL])
        assertEquals("google", map[ActivationDeviceProfileHeaders.DEVICE_BRAND])
        assertEquals("14", map[ActivationDeviceProfileHeaders.OS_VERSION])
        assertEquals("34", map[ActivationDeviceProfileHeaders.OS_SDK_INT])
        assertEquals("1.0.0", map[ActivationDeviceProfileHeaders.CLIENT_VERSION_NAME])
        assertEquals("zh_CN", map[ActivationDeviceProfileHeaders.DEVICE_LOCALE])
        assertTrue(ActivationDeviceProfileHeaders.DEVICE_MANUFACTURER !in map)
    }
}

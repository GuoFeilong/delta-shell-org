package com.delta.core.activation.context

import android.os.Build
import java.util.Locale

class AndroidDeviceProfileProvider : DeviceProfileProvider {

    override fun snapshot(): DeviceProfileSnapshot =
        DeviceProfileSnapshot(
            deviceModel = Build.MODEL?.trim()?.takeIf { it.isNotEmpty() },
            deviceBrand = Build.BRAND?.trim()?.takeIf { it.isNotEmpty() },
            deviceManufacturer = Build.MANUFACTURER?.trim()?.takeIf { it.isNotEmpty() },
            osVersion = Build.VERSION.RELEASE?.trim()?.takeIf { it.isNotEmpty() },
            osSdkInt = Build.VERSION.SDK_INT.takeIf { it > 0 }?.toString(),
            locale = Locale.getDefault().toString().trim().takeIf { it.isNotEmpty() },
        )
}

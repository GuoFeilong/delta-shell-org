package com.delta.helper

import android.app.Application
import android.util.Log
import com.delta.helper.activation.DeviceProfileSynchronizer
import com.delta.helper.config.HelperBuildFlags
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HelperApplication : Application() {
    @Inject lateinit var deviceProfileSynchronizer: DeviceProfileSynchronizer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Build flags:\n${HelperBuildFlags.debugSummary()}")
        }
        deviceProfileSynchronizer.scheduleSync()
    }

    private companion object {
        const val TAG = "DeltaHelper"
    }
}

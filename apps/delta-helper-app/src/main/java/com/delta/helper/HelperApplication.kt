package com.delta.helper

import android.app.Application
import android.util.Log
import com.delta.helper.config.HelperBuildFlags
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Build flags:\n${HelperBuildFlags.debugSummary()}")
        }
    }

    private companion object {
        const val TAG = "DeltaHelper"
    }
}

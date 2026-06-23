package com.delta.helper

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelperApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.i(
                TAG,
                "dailyDebug API=${BuildConfig.API_BASE_URL} " +
                    "(emulator: ensure delta-api listens on :8080; " +
                    "try `adb reverse tcp:8080 tcp:8080` if connect times out)",
            )
        }
    }

    private companion object {
        const val TAG = "DeltaDev"
    }
}

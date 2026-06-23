package com.delta.helper.overlay

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity

object OverlayLaunchCoordinator {
    @Volatile
    var isOverlayVisible: Boolean = false
        private set

    internal fun markOverlayVisible(visible: Boolean) {
        isOverlayVisible = visible
    }

    fun enterGameAssistMode(activity: ComponentActivity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        activity.window.decorView.post {
            activity.moveTaskToBack(true)
        }
    }

    fun restoreDefaultOrientation(activity: Activity) {
        if (!isOverlayVisible) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

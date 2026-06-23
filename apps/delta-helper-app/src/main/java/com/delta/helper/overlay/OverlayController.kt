package com.delta.helper.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object OverlayController {
    const val ACTION_SHOW = "com.delta.helper.overlay.SHOW"
    const val ACTION_HIDE = "com.delta.helper.overlay.HIDE"

    fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun overlayPermissionIntent(context: Context): Intent =
        Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}"),
        )

    fun show(context: Context, session: OverlaySession) {
        val intent = Intent(context, FloatingOverlayService::class.java).apply {
            action = ACTION_SHOW
            session.writeTo(this)
        }
        context.startService(intent)
    }

    fun hide(context: Context) {
        val intent = Intent(context, FloatingOverlayService::class.java).apply {
            action = ACTION_HIDE
        }
        context.startService(intent)
    }
}

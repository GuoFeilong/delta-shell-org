package com.delta.core.activation.device

import android.content.Context
import com.google.android.gms.appset.AppSet
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Fetches the Google Play App Set ID — the recommended replacement for hardware identifiers.
 *
 * Scope is typically [com.google.android.gms.appset.AppSetIdInfo.SCOPE_DEVELOPER] on
 * devices with Google Play services: shared across apps from the same Play Console
 * developer until all of those apps are uninstalled.
 */
internal class AppSetIdFetcher(
    context: Context,
) {
    private val appContext = context.applicationContext

    suspend fun fetchOrNull(): String? = suspendCancellableCoroutine { continuation ->
        AppSet.getClient(appContext).appSetIdInfo
            .addOnSuccessListener { info ->
                val id = info.id.trim().takeIf { it.isNotEmpty() }
                continuation.resume(id)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }
}

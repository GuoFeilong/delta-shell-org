package com.delta.core.activation.device

import java.util.UUID

/**
 * Resolves the final device ID on first launch.
 *
 * Priority: App Set ID (via [AppSetIdFetcher]) → random UUID.
 * The chosen value is persisted in DataStore by [DeviceIdStore] and never recomputed.
 */
internal object DeviceIdGenerator {
    fun resolve(externalId: String?): String {
        val trimmed = externalId?.trim().orEmpty()
        if (trimmed.isNotEmpty()) return trimmed
        return UUID.randomUUID().toString()
    }
}

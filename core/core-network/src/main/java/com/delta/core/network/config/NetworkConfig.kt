package com.delta.core.network.config

import java.util.concurrent.TimeUnit

/**
 * Network client configuration contract. App/feature modules provide an implementation
 * (e.g. [com.delta.group.network.AppNetworkConfig]) and bind it via Hilt.
 */
interface NetworkConfig {
    val baseUrl: String
    val headers: Map<String, String>
    val hostHeader: String?
    val connectTimeoutSeconds: Long
    val readTimeoutSeconds: Long
    val writeTimeoutSeconds: Long
    val loggingEnabled: Boolean

    val normalizedBaseUrl: String
        get() = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

    val connectTimeoutMillis: Long
        get() = TimeUnit.SECONDS.toMillis(connectTimeoutSeconds)

    val readTimeoutMillis: Long
        get() = TimeUnit.SECONDS.toMillis(readTimeoutSeconds)

    val writeTimeoutMillis: Long
        get() = TimeUnit.SECONDS.toMillis(writeTimeoutSeconds)

    companion object {
        const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 30L
        const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
        const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L
    }
}

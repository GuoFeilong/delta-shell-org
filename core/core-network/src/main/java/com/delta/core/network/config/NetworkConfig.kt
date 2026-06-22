package com.delta.core.network.config

import java.util.concurrent.TimeUnit

/**
 * Describes how a business module wants its HTTP client configured.
 *
 * Each feature/app module can create its own [NetworkConfig] with a different
 * [baseUrl], [headers], or [hostHeader], then pass it to [com.delta.core.network.factory.NetworkClientFactory].
 */
data class NetworkConfig(
    val baseUrl: String,
    val headers: Map<String, String> = emptyMap(),
    val hostHeader: String? = null,
    val connectTimeoutSeconds: Long = DEFAULT_CONNECT_TIMEOUT_SECONDS,
    val readTimeoutSeconds: Long = DEFAULT_READ_TIMEOUT_SECONDS,
    val writeTimeoutSeconds: Long = DEFAULT_WRITE_TIMEOUT_SECONDS,
    val loggingEnabled: Boolean = false,
) {
    init {
        require(baseUrl.isNotBlank()) { "baseUrl must not be blank" }
        require(connectTimeoutSeconds > 0) { "connectTimeoutSeconds must be positive" }
        require(readTimeoutSeconds > 0) { "readTimeoutSeconds must be positive" }
        require(writeTimeoutSeconds > 0) { "writeTimeoutSeconds must be positive" }
    }

    val normalizedBaseUrl: String
        get() = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

    val connectTimeoutMillis: Long = TimeUnit.SECONDS.toMillis(connectTimeoutSeconds)
    val readTimeoutMillis: Long = TimeUnit.SECONDS.toMillis(readTimeoutSeconds)
    val writeTimeoutMillis: Long = TimeUnit.SECONDS.toMillis(writeTimeoutSeconds)

    companion object {
        const val DEFAULT_CONNECT_TIMEOUT_SECONDS = 30L
        const val DEFAULT_READ_TIMEOUT_SECONDS = 30L
        const val DEFAULT_WRITE_TIMEOUT_SECONDS = 30L
    }
}

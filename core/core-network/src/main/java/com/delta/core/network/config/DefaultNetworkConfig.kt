package com.delta.core.network.config

import com.delta.core.network.logging.HttpLoggingConfig

/**
 * Default [NetworkConfig] implementation created by [networkConfig] DSL or manually.
 */
data class DefaultNetworkConfig(
    override val baseUrl: String,
    override val headers: Map<String, String> = emptyMap(),
    override val hostHeader: String? = null,
    override val connectTimeoutSeconds: Long = NetworkConfig.DEFAULT_CONNECT_TIMEOUT_SECONDS,
    override val readTimeoutSeconds: Long = NetworkConfig.DEFAULT_READ_TIMEOUT_SECONDS,
    override val writeTimeoutSeconds: Long = NetworkConfig.DEFAULT_WRITE_TIMEOUT_SECONDS,
    override val loggingEnabled: Boolean = false,
    override val httpLoggingConfig: HttpLoggingConfig = HttpLoggingConfig.DEFAULT,
) : NetworkConfig {
    init {
        require(baseUrl.isNotBlank()) { "baseUrl must not be blank" }
        require(connectTimeoutSeconds > 0) { "connectTimeoutSeconds must be positive" }
        require(readTimeoutSeconds > 0) { "readTimeoutSeconds must be positive" }
        require(writeTimeoutSeconds > 0) { "writeTimeoutSeconds must be positive" }
    }
}

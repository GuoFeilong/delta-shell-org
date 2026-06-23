package com.delta.helper.network

import com.delta.core.network.config.NetworkConfig
import com.delta.helper.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNetworkConfig @Inject constructor() : NetworkConfig {
    override val baseUrl: String = BuildConfig.API_BASE_URL
    override val headers: Map<String, String> = mapOf(
        "X-App-Id" to BuildConfig.APPLICATION_ID,
        "X-Client-Version" to BuildConfig.VERSION_NAME,
    )
    override val hostHeader: String? = BuildConfig.API_HOST_HEADER.ifBlank { null }
    override val connectTimeoutSeconds: Long = NetworkConfig.DEFAULT_CONNECT_TIMEOUT_SECONDS
    override val readTimeoutSeconds: Long = NetworkConfig.DEFAULT_READ_TIMEOUT_SECONDS
    override val writeTimeoutSeconds: Long = NetworkConfig.DEFAULT_WRITE_TIMEOUT_SECONDS
    override val loggingEnabled: Boolean = BuildConfig.DEBUG
}

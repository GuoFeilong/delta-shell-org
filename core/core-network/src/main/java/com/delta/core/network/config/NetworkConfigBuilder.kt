package com.delta.core.network.config

import com.delta.core.network.logging.HttpLoggingConfig

/**
 * Fluent builder for [NetworkConfig]. Business modules use this to declare host,
 * headers, and other HTTP parameters without touching OkHttp/Retrofit directly.
 *
 * Example:
 * ```
 * val config = networkConfig {
 *     baseUrl("https://api.example.com")
 *     header("Authorization", "Bearer $token")
 *     header("X-Client-Version", BuildConfig.VERSION_NAME)
 *     hostHeader("api.example.com") // optional reverse-proxy override
 *     loggingEnabled(BuildConfig.DEBUG)
 *     httpLoggingTag("DeltaHttp") // Logcat filter: tag:DeltaHttp
 *     includeHttpPaths("/activation") // optional path filter
 * }
 * ```
 */
class NetworkConfigBuilder {
    private var baseUrl: String = ""
    private val headers = linkedMapOf<String, String>()
    private var hostHeader: String? = null
    private var connectTimeoutSeconds: Long = NetworkConfig.DEFAULT_CONNECT_TIMEOUT_SECONDS
    private var readTimeoutSeconds: Long = NetworkConfig.DEFAULT_READ_TIMEOUT_SECONDS
    private var writeTimeoutSeconds: Long = NetworkConfig.DEFAULT_WRITE_TIMEOUT_SECONDS
    private var loggingEnabled: Boolean = false
    private var httpLoggingConfig: HttpLoggingConfig = HttpLoggingConfig.DEFAULT

    fun baseUrl(url: String) = apply {
        baseUrl = url
    }

    fun header(name: String, value: String) = apply {
        headers[name] = value
    }

    fun headers(values: Map<String, String>) = apply {
        headers.putAll(values)
    }

    /**
     * Overrides the HTTP `Host` header while keeping [baseUrl] as the request target.
     * Useful when connecting via IP or a gateway but the upstream expects a virtual host.
     */
    fun hostHeader(host: String) = apply {
        hostHeader = host
    }

    fun connectTimeoutSeconds(seconds: Long) = apply {
        connectTimeoutSeconds = seconds
    }

    fun readTimeoutSeconds(seconds: Long) = apply {
        readTimeoutSeconds = seconds
    }

    fun writeTimeoutSeconds(seconds: Long) = apply {
        writeTimeoutSeconds = seconds
    }

    fun loggingEnabled(enabled: Boolean) = apply {
        loggingEnabled = enabled
    }

    fun httpLoggingConfig(config: HttpLoggingConfig) = apply {
        httpLoggingConfig = config
    }

    fun httpLoggingTag(tag: String) = apply {
        httpLoggingConfig = httpLoggingConfig.copy(tag = tag)
    }

    fun includeHttpPaths(vararg paths: String) = apply {
        httpLoggingConfig = httpLoggingConfig.copy(includePathPatterns = paths.toList())
    }

    fun excludeHttpPaths(vararg paths: String) = apply {
        httpLoggingConfig = httpLoggingConfig.copy(excludePathPatterns = paths.toList())
    }

    fun build(): NetworkConfig = DefaultNetworkConfig(
        baseUrl = baseUrl,
        headers = headers.toMap(),
        hostHeader = hostHeader,
        connectTimeoutSeconds = connectTimeoutSeconds,
        readTimeoutSeconds = readTimeoutSeconds,
        writeTimeoutSeconds = writeTimeoutSeconds,
        loggingEnabled = loggingEnabled,
        httpLoggingConfig = httpLoggingConfig,
    )
}

fun networkConfig(block: NetworkConfigBuilder.() -> Unit): NetworkConfig =
    NetworkConfigBuilder().apply(block).build()

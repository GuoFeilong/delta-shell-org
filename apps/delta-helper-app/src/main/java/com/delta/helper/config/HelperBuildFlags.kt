package com.delta.helper.config

import com.delta.helper.BuildConfig

/**
 * 构建期开关汇总（见根目录 gradle.properties.local.example）。
 */
object HelperBuildFlags {
    val buildEnv: String get() = BuildConfig.BUILD_ENV
    val isDaily: Boolean get() = buildEnv == "daily"
    val isOnline: Boolean get() = buildEnv == "online"

    val apiBaseUrl: String get() = BuildConfig.API_BASE_URL
    val apiHostHeader: String? get() = BuildConfig.API_HOST_HEADER.ifBlank { null }

    /** dailyDebug 默认 true：跳过激活直接可用 */
    val mockAlreadyActivated: Boolean get() = BuildConfig.MOCK_ALREADY_ACTIVATED

    /** dailyDebug 默认 true：本地模拟未激活，兑换后写本地 session */
    val simulateNotActivated: Boolean get() = BuildConfig.SIMULATE_NOT_ACTIVATED

    val httpLoggingEnabled: Boolean get() = BuildConfig.HTTP_LOGGING_ENABLED

    fun debugSummary(): String = buildString {
        appendLine("buildEnv=$buildEnv")
        appendLine("api=$apiBaseUrl")
        appendLine("hostHeader=${apiHostHeader ?: "(none)"}")
        appendLine("channel=${BuildConfig.CLIENT_CHANNEL}")
        appendLine("publisher=${BuildConfig.PUBLISHER_KEY}")
        appendLine("mockAlreadyActivated=$mockAlreadyActivated")
        appendLine("simulateNotActivated=$simulateNotActivated")
        appendLine("httpLogging=$httpLoggingEnabled")
    }
}

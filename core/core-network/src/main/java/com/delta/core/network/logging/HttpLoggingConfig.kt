package com.delta.core.network.logging

/**
 * Debug HTTP logging options. Use [tag] in Logcat filter, e.g. `tag:DeltaHttp`.
 */
data class HttpLoggingConfig(
    val tag: String = DEFAULT_TAG,
    val includePathPatterns: List<String> = emptyList(),
    val excludePathPatterns: List<String> = emptyList(),
    val redactedHeaderNames: Set<String> = DEFAULT_REDACTED_HEADERS,
    val logHeaders: Boolean = true,
    val logBody: Boolean = true,
    val prettyPrintJson: Boolean = true,
) {
    fun shouldLogPath(encodedPath: String): Boolean {
        if (excludePathPatterns.any { encodedPath.contains(it, ignoreCase = true) }) {
            return false
        }
        if (includePathPatterns.isEmpty()) {
            return true
        }
        return includePathPatterns.any { encodedPath.contains(it, ignoreCase = true) }
    }

    fun redactHeader(name: String, value: String): String =
        if (name.lowercase() in REDACTED_HEADER_LOOKUP) REDACTED_VALUE else value

    companion object {
        const val DEFAULT_TAG = "DeltaHttp"
        const val REDACTED_VALUE = "██REDACTED██"

        val DEFAULT_REDACTED_HEADERS: Set<String> = setOf(
            "Authorization",
            "Cookie",
            "Set-Cookie",
            "X-Api-Key",
            "X-Auth-Token",
        )

        private val REDACTED_HEADER_LOOKUP: Set<String> =
            DEFAULT_REDACTED_HEADERS.map { it.lowercase() }.toSet()

        val DEFAULT = HttpLoggingConfig()
    }
}

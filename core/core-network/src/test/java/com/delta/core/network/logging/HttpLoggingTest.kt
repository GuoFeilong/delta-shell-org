package com.delta.core.network.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpLoggingConfigTest {
    private val config = HttpLoggingConfig(
        includePathPatterns = listOf("/activation"),
        excludePathPatterns = listOf("/health"),
    )

    @Test
    fun shouldLogPath_whenIncludedAndNotExcluded() {
        assertTrue(config.shouldLogPath("/api/v1/activation/status"))
    }

    @Test
    fun shouldNotLogPath_whenExcluded() {
        assertFalse(config.shouldLogPath("/api/v1/activation/health"))
    }

    @Test
    fun shouldNotLogPath_whenNotIncluded() {
        assertFalse(config.shouldLogPath("/api/v1/graphics/games"))
    }

    @Test
    fun redactsSensitiveHeaders() {
        assertEquals(
            HttpLoggingConfig.REDACTED_VALUE,
            config.redactHeader("Authorization", "Bearer secret"),
        )
        assertEquals("daily", config.redactHeader("X-Client-Channel", "daily"))
    }
}

class HttpLogFormatterTest {
    private val config = HttpLoggingConfig(prettyPrintJson = true)

    @Test
    fun prettyPrintsJsonBody() {
        val formatted = HttpLogFormatter.formatBody("""{"code":0,"data":{"activated":true}}""", config)
        assertTrue(formatted.contains("\n"))
        assertTrue(formatted.contains("\"code\": 0"))
    }

    @Test
    fun keepsNonJsonBodyUnchanged() {
        val raw = "plain-text-body"
        assertEquals(raw, HttpLogFormatter.formatBody(raw, config))
    }
}

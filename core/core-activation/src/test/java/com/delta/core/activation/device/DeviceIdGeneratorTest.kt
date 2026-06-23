package com.delta.core.activation.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceIdGeneratorTest {

    @Test
    fun resolve_prefersExternalIdWhenPresent() {
        assertEquals(
            "01234567-89ab-cdef-0123-456789abcdef",
            DeviceIdGenerator.resolve("01234567-89ab-cdef-0123-456789abcdef"),
        )
    }

    @Test
    fun resolve_trimsExternalId() {
        assertEquals(
            "abc",
            DeviceIdGenerator.resolve("  abc  "),
        )
    }

    @Test
    fun resolve_fallsBackToUuidWhenExternalIdMissing() {
        val resolved = DeviceIdGenerator.resolve(null)
        assertTrue(resolved.matches(UUID_PATTERN))
    }

    companion object {
        private val UUID_PATTERN =
            Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    }
}

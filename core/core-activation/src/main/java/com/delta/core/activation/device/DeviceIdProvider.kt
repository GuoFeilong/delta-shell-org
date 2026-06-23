package com.delta.core.activation.device

/**
 * Provides a stable device identifier for activation APIs.
 *
 * Implementations must return the same value across app restarts until reinstall.
 */
interface DeviceIdProvider {
    suspend fun getDeviceId(): String
}

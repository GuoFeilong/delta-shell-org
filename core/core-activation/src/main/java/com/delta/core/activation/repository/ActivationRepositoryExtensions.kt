package com.delta.core.activation.repository

import com.delta.core.activation.model.ActivationStatus
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.first

/**
 * Queries activation status and uploads device profile headers (model, OS, app version, etc.).
 */
suspend fun ActivationRepository.awaitActivationStatus(): ApiResult<ActivationStatus> =
    getActivationStatus().first { it !is ApiResult.Loading }

/**
 * Best-effort device profile sync; ignores network failures.
 */
suspend fun ActivationRepository.syncDeviceProfile(): ApiResult<ActivationStatus> =
    awaitActivationStatus()

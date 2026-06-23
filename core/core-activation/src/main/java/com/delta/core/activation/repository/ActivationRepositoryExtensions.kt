package com.delta.core.activation.repository

import com.delta.core.activation.model.ActivationStatus
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.first

suspend fun ActivationRepository.awaitActivationStatus(): ApiResult<ActivationStatus> =
    getActivationStatus().first { it !is ApiResult.Loading }

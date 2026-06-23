package com.delta.core.activation.repository

import com.delta.core.activation.api.ext.ReleaseGateApi
import com.delta.core.activation.context.ActivationHeaderFactory
import com.delta.core.activation.model.ReleaseGate
import com.delta.core.activation.model.toDomain
import com.delta.core.network.flow.ApiCallExecutor
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.Flow

class ReleaseGateRepository(
    private val apiCallExecutor: ApiCallExecutor,
    private val releaseGateApi: ReleaseGateApi,
    private val headerFactory: ActivationHeaderFactory,
) {
    fun evaluate(): Flow<ApiResult<ReleaseGate>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            releaseGateApi.evaluate(
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
            )
        }.mapSuccess { it.toDomain() }
}

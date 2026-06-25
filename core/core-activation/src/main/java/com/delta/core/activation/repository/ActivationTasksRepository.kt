package com.delta.core.activation.repository

import com.delta.core.activation.api.ext.ActivationTasksApi
import com.delta.core.activation.context.ActivationHeaderFactory
import com.delta.core.activation.context.toDeviceProfileHeaderMap
import com.delta.core.activation.model.ActivationStatus
import com.delta.core.activation.model.TaskList
import com.delta.core.activation.model.TaskVerifyResult
import com.delta.core.activation.model.dto.TaskCompleteRequestDto
import com.delta.core.activation.model.dto.TaskStepCardRedeemRequestDto
import com.delta.core.activation.model.dto.TaskVerifyRequestDto
import com.delta.core.activation.model.toDomain
import com.delta.core.network.flow.ApiCallExecutor
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.Flow

class ActivationTasksRepository(
    private val apiCallExecutor: ApiCallExecutor,
    private val activationTasksApi: ActivationTasksApi,
    private val headerFactory: ActivationHeaderFactory,
) {
    fun listTasks(): Flow<ApiResult<TaskList>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            val profile = headers.toDeviceProfileHeaderMap()
            activationTasksApi.listTasks(
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
                deviceProfileHeaders = profile,
            )
        }.mapSuccess { it.toDomain() }

    fun verifyStep(stepId: Long, answer: String): Flow<ApiResult<TaskVerifyResult>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            val profile = headers.toDeviceProfileHeaderMap()
            activationTasksApi.verifyStep(
                stepId = stepId,
                request = TaskVerifyRequestDto(answer = answer.trim()),
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
                deviceProfileHeaders = profile,
            )
        }.mapSuccess { it.toDomain() }

    fun redeemStepCard(stepId: Long, cardCode: String): Flow<ApiResult<TaskVerifyResult>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            val profile = headers.toDeviceProfileHeaderMap()
            activationTasksApi.redeemStepCard(
                stepId = stepId,
                request = TaskStepCardRedeemRequestDto(cardCode = cardCode.trim()),
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
                deviceProfileHeaders = profile,
            )
        }.mapSuccess { it.toDomain() }

    fun completeTasks(progressToken: String): Flow<ApiResult<ActivationStatus>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            val profile = headers.toDeviceProfileHeaderMap()
            activationTasksApi.completeTasks(
                request = TaskCompleteRequestDto(progressToken = progressToken),
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
                deviceProfileHeaders = profile,
            )
        }.mapSuccess { it.toDomain() }
}

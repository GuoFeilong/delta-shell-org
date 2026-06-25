package com.delta.core.activation.repository

import com.delta.core.activation.api.ActivationApi
import com.delta.core.activation.context.ActivationHeaderFactory
import com.delta.core.activation.context.toDeviceProfileHeaderMap
import com.delta.core.activation.model.ActivationStatus
import com.delta.core.activation.model.CardPurchaseOptions
import com.delta.core.activation.model.CardPurchaseUrl
import com.delta.core.activation.model.dto.CardRedeemRequestDto
import com.delta.core.activation.model.toDomain
import com.delta.core.network.flow.ApiCallExecutor
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.Flow

class ActivationRepository(
    private val apiCallExecutor: ApiCallExecutor,
    private val activationApi: ActivationApi,
    private val headerFactory: ActivationHeaderFactory,
    private val clientContext: com.delta.core.activation.context.ActivationClientContext,
) {
    fun getActivationStatus(): Flow<ApiResult<ActivationStatus>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            activationApi.getStatus(
                deviceId = headers.deviceId,
                appPackageName = clientContext.appPackageName,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                deviceProfileHeaders = headers.toDeviceProfileHeaderMap(),
            )
        }.mapSuccess { it.toDomain() }

    fun getPurchaseUrl(): Flow<ApiResult<CardPurchaseUrl>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            activationApi.getPurchaseUrl(
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
            )
        }.mapSuccess { it.toDomain() }

    fun getPurchaseOptions(): Flow<ApiResult<CardPurchaseOptions>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            activationApi.getPurchaseOptions(
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
            )
        }.mapSuccess { it.toDomain() }

    fun redeemCard(cardCode: String): Flow<ApiResult<ActivationStatus>> =
        apiCallExecutor.asEnvelopeFlow {
            val headers = headerFactory.build()
            activationApi.redeemCard(
                request = CardRedeemRequestDto(
                    cardCode = cardCode.trim(),
                    appPackageName = clientContext.appPackageName,
                ),
                deviceId = headers.deviceId,
                clientOs = headers.clientOs,
                clientChannel = headers.clientChannel,
                clientVersion = headers.clientVersion,
                publisherKey = headers.publisherKey,
                appPackageName = clientContext.appPackageName,
                deviceProfileHeaders = headers.toDeviceProfileHeaderMap(),
            )
        }.mapSuccess { it.toDomain() }
}

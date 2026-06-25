package com.delta.core.activation.api

import com.delta.core.activation.model.dto.ActivationStatusDto
import com.delta.core.activation.model.dto.CardPurchaseUrlDto
import com.delta.core.activation.model.dto.CardRedeemRequestDto
import com.delta.core.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ActivationApi {
    @GET(ActivationApiPaths.STATUS)
    suspend fun getStatus(
        @Header("X-Device-Id") deviceId: String,
        @Header("X-App-Package-Name") appPackageName: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<ActivationStatusDto>

    @GET(ActivationApiPaths.PURCHASE_URL)
    suspend fun getPurchaseUrl(
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
    ): ApiResponse<CardPurchaseUrlDto>

    @POST(ActivationApiPaths.REDEEM)
    suspend fun redeemCard(
        @Body request: CardRedeemRequestDto,
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
        @Header("X-App-Package-Name") appPackageName: String,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<ActivationStatusDto>
}

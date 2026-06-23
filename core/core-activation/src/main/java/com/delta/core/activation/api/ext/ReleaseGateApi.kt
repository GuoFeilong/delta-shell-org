package com.delta.core.activation.api.ext

import com.delta.core.activation.api.ActivationApiPaths
import com.delta.core.activation.model.dto.ReleaseGateDto
import com.delta.core.network.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ReleaseGateApi {
    @GET(ActivationApiPaths.RELEASE_GATE)
    suspend fun evaluate(
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
    ): ApiResponse<ReleaseGateDto>
}

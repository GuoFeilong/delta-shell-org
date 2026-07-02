package com.delta.core.activation.api.ext

import com.delta.core.activation.api.ActivationApiPaths
import com.delta.core.activation.model.dto.ActivationStatusDto
import com.delta.core.activation.model.dto.TaskCompleteRequestDto
import com.delta.core.activation.model.dto.TaskListDto
import com.delta.core.activation.model.dto.TaskStepCardRedeemRequestDto
import com.delta.core.activation.model.dto.TaskVerifyRequestDto
import com.delta.core.activation.model.dto.TaskVerifyResultDto
import com.delta.core.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface ActivationTasksApi {
    @GET(ActivationApiPaths.TASKS)
    suspend fun listTasks(
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<TaskListDto>

    @POST(ActivationApiPaths.TASK_VERIFY)
    suspend fun verifyStep(
        @Path("stepId") stepId: Long,
        @Body request: TaskVerifyRequestDto,
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<TaskVerifyResultDto>

    @POST(ActivationApiPaths.TASK_STEP_REDEEM)
    suspend fun redeemStepCard(
        @Path("stepId") stepId: Long,
        @Body request: TaskStepCardRedeemRequestDto,
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<TaskVerifyResultDto>

    @POST(ActivationApiPaths.TASKS_COMPLETE)
    suspend fun completeTasks(
        @Body request: TaskCompleteRequestDto,
        @Header("X-Device-Id") deviceId: String,
        @Header("X-Client-OS") clientOs: String,
        @Header("X-Client-Channel") clientChannel: String,
        @Header("X-Client-Version") clientVersion: String? = null,
        @Header("X-Publisher-Key") publisherKey: String? = null,
        @Header("X-App-Package-Name") appPackageName: String? = null,
        @HeaderMap deviceProfileHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<ActivationStatusDto>
}

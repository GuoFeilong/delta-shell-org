package com.delta.core.network.flow

import com.delta.core.network.model.ApiBusinessException
import com.delta.core.network.model.ApiNetworkException
import com.delta.core.network.model.ApiResponse
import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps Retrofit suspend calls into [Flow] of [ApiResult].
 *
 * Usage:
 * ```
 * apiCallExecutor.asFlow { homeApi.feed() }
 * apiCallExecutor.asEnvelopeFlow { homeApi.feedEnvelope() }
 * ```
 */
@Singleton
class ApiCallExecutor @Inject constructor() {

    fun <T> asFlow(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        request: suspend () -> T,
    ): Flow<ApiResult<T>> = flow {
        emit(ApiResult.Loading)
        try {
            val data = withContext(dispatcher) { request() }
            emit(ApiResult.Success(data))
        } catch (throwable: Throwable) {
            emit(throwable.toApiError())
        }
    }.flowOn(dispatcher)

    fun <T> asEnvelopeFlow(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        request: suspend () -> ApiResponse<T>,
    ): Flow<ApiResult<T>> = asFlow(dispatcher) {
        val envelope = request()
        if (envelope.isSuccess) {
            envelope.data ?: throw ApiBusinessException(
                code = envelope.code,
                message = envelope.message.ifBlank { "Response data is null" },
            )
        } else {
            throw ApiBusinessException(
                code = envelope.code,
                message = envelope.message.ifBlank { "Request failed" },
            )
        }
    }

    private fun Throwable.toApiError(): ApiResult.Error = when (this) {
        is ApiBusinessException -> ApiResult.Error(code = code, message = message, throwable = this)
        is HttpException -> ApiResult.Error(
            code = code(),
            message = message().ifBlank { "HTTP ${code()}" },
            throwable = this,
        )
        is IOException -> ApiResult.Error(
            code = -1,
            message = ApiNetworkException(message ?: "Network error", this).message,
            throwable = this,
        )
        else -> ApiResult.Error(
            code = -1,
            message = message ?: "Unknown error",
            throwable = this,
        )
    }
}

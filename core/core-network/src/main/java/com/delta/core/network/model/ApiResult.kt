package com.delta.core.network.model

/**
 * Unified API call result emitted through [Flow][kotlinx.coroutines.flow.Flow].
 */
sealed interface ApiResult<out T> {
    data object Loading : ApiResult<Nothing>

    data class Success<T>(val data: T) : ApiResult<T>

    data class Error(
        val code: Int,
        val message: String,
        val throwable: Throwable? = null,
    ) : ApiResult<Nothing>
}

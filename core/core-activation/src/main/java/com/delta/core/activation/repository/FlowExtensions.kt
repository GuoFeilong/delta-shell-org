package com.delta.core.activation.repository

import com.delta.core.network.model.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun <T, R> Flow<ApiResult<T>>.mapSuccess(
    transform: (T) -> R,
): Flow<ApiResult<R>> = map { result ->
    when (result) {
        ApiResult.Loading -> ApiResult.Loading
        is ApiResult.Success -> ApiResult.Success(transform(result.data))
        is ApiResult.Error -> result
    }
}

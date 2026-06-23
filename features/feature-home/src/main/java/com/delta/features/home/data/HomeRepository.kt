package com.delta.features.home.data

import com.delta.core.network.flow.ApiCallExecutor
import com.delta.core.network.model.ApiResult
import com.delta.features.home.data.remote.GreetingDto
import com.delta.features.home.data.remote.HomeApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiCallExecutor: ApiCallExecutor,
    private val homeApi: HomeApi,
) {
    fun loadGreeting(): Flow<ApiResult<GreetingDto>> =
        apiCallExecutor.asFlow { homeApi.getGreeting() }
}

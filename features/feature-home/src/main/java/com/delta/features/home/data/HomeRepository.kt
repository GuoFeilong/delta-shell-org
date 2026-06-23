package com.delta.features.home.data

import com.delta.core.network.flow.ApiCallExecutor
import com.delta.core.network.model.ApiResult
import com.delta.features.home.data.remote.HomeApi
import com.delta.features.home.data.remote.GreetingDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiCallExecutor: ApiCallExecutor,
    retrofit: Retrofit,
) {
    private val homeApi: HomeApi = retrofit.create(HomeApi::class.java)

    fun loadGreeting(): Flow<ApiResult<GreetingDto>> =
        apiCallExecutor.asFlow { homeApi.getGreeting() }
}

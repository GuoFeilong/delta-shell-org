package com.delta.features.home.data.repository

import com.delta.features.home.data.api.HomeApi
import com.delta.features.home.data.model.HomeFeedResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val homeApi: HomeApi,
) {
    suspend fun loadFeed(): HomeFeedResponse = homeApi.getFeed()
}

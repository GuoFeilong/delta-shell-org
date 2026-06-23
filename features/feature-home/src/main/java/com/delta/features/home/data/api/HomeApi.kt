package com.delta.features.home.data.api

import com.delta.features.home.data.model.HomeFeedResponse
import retrofit2.http.GET

interface HomeApi {
    @GET("home/feed")
    suspend fun getFeed(): HomeFeedResponse
}

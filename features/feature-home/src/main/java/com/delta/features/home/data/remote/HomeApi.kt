package com.delta.features.home.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface HomeApi {
    @GET("greeting")
    suspend fun getGreeting(): GreetingDto
}

@Serializable
data class GreetingDto(val message: String)

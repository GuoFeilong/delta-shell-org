package com.delta.features.home.di

import com.delta.core.network.config.networkConfig
import com.delta.core.network.factory.NetworkClientFactory
import com.delta.features.home.BuildConfig
import com.delta.features.home.data.api.HomeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Wires [HomeApi] through the DI-free [NetworkClientFactory].
 *
 * `core-network` stays a pure builder; this module owns business-specific config.
 */
@Module
@InstallIn(SingletonComponent::class)
object HomeNetworkModule {
    @Provides
    @Singleton
    fun provideHomeApi(): HomeApi {
        val config = networkConfig {
            baseUrl(BuildConfig.HOME_API_BASE_URL)
            loggingEnabled(BuildConfig.DEBUG)
        }
        return NetworkClientFactory.createApiService(config)
    }
}

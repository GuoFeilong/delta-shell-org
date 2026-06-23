package com.delta.features.home.di

import com.delta.core.network.config.NetworkConfig
import com.delta.core.network.factory.NetworkClientFactory
import com.delta.features.home.data.remote.HomeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Feature-level Hilt wiring. `core-network` only exposes [NetworkClientFactory].
 */
@Module
@InstallIn(SingletonComponent::class)
object HomeNetworkModule {

    @Provides
    @Singleton
    fun provideHomeApi(config: NetworkConfig): HomeApi =
        NetworkClientFactory.createApiService(config)
}

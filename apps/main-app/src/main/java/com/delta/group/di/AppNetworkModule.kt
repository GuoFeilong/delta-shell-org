package com.delta.group.di

import com.delta.core.network.config.NetworkConfig
import com.delta.core.network.flow.ApiCallExecutor
import com.delta.group.network.AppNetworkConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConfig(impl: AppNetworkConfig): NetworkConfig

    companion object {
        @Provides
        @Singleton
        fun provideApiCallExecutor(): ApiCallExecutor = ApiCallExecutor()
    }
}

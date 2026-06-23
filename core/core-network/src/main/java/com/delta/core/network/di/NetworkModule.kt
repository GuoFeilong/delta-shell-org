package com.delta.core.network.di

import com.delta.core.network.config.NetworkConfig
import com.delta.core.network.factory.NetworkClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(config: NetworkConfig): OkHttpClient =
        NetworkClientFactory.createOkHttpClient(config)

    @Provides
    @Singleton
    fun provideRetrofit(
        config: NetworkConfig,
        okHttpClient: OkHttpClient,
    ): Retrofit = NetworkClientFactory.createRetrofit(
        config = config,
        okHttpClient = okHttpClient,
    )
}

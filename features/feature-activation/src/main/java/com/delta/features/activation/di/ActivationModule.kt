package com.delta.features.activation.di

import android.app.Application
import com.delta.core.activation.api.ActivationApi
import com.delta.core.activation.api.ext.ActivationTasksApi
import com.delta.core.activation.api.ext.ReleaseGateApi
import com.delta.core.activation.context.ActivationClientContext
import com.delta.core.activation.context.ActivationHeaderFactory
import com.delta.core.activation.device.DeviceIdProvider
import com.delta.core.activation.device.DeviceIdStore
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.activation.repository.ActivationTasksRepository
import com.delta.core.activation.repository.ReleaseGateRepository
import com.delta.core.network.config.NetworkConfig
import com.delta.core.network.factory.NetworkClientFactory
import com.delta.core.network.flow.ApiCallExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivationModule {

    @Provides
    @Singleton
    fun provideDeviceIdProvider(
        application: Application,
    ): DeviceIdProvider = DeviceIdStore(application)

    @Provides
    @Singleton
    fun provideActivationHeaderFactory(
        clientContext: ActivationClientContext,
        deviceIdProvider: DeviceIdProvider,
    ): ActivationHeaderFactory = ActivationHeaderFactory(
        clientContext = clientContext,
        deviceIdProvider = deviceIdProvider,
    )

    @Provides
    @Singleton
    fun provideActivationApi(
        config: NetworkConfig,
    ): ActivationApi = NetworkClientFactory.createApiService(config)

    @Provides
    @Singleton
    fun provideReleaseGateApi(
        config: NetworkConfig,
    ): ReleaseGateApi = NetworkClientFactory.createApiService(config)

    @Provides
    @Singleton
    fun provideActivationTasksApi(
        config: NetworkConfig,
    ): ActivationTasksApi = NetworkClientFactory.createApiService(config)

    @Provides
    @Singleton
    fun provideActivationRepository(
        apiCallExecutor: ApiCallExecutor,
        activationApi: ActivationApi,
        headerFactory: ActivationHeaderFactory,
        clientContext: ActivationClientContext,
    ): ActivationRepository = ActivationRepository(
        apiCallExecutor = apiCallExecutor,
        activationApi = activationApi,
        headerFactory = headerFactory,
        clientContext = clientContext,
    )

    @Provides
    @Singleton
    fun provideReleaseGateRepository(
        apiCallExecutor: ApiCallExecutor,
        releaseGateApi: ReleaseGateApi,
        headerFactory: ActivationHeaderFactory,
    ): ReleaseGateRepository = ReleaseGateRepository(
        apiCallExecutor = apiCallExecutor,
        releaseGateApi = releaseGateApi,
        headerFactory = headerFactory,
    )

    @Provides
    @Singleton
    fun provideActivationTasksRepository(
        apiCallExecutor: ApiCallExecutor,
        activationTasksApi: ActivationTasksApi,
        headerFactory: ActivationHeaderFactory,
    ): ActivationTasksRepository = ActivationTasksRepository(
        apiCallExecutor = apiCallExecutor,
        activationTasksApi = activationTasksApi,
        headerFactory = headerFactory,
    )
}

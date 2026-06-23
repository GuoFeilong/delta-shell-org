package com.delta.group.di

import com.delta.core.activation.context.ActivationClientContext
import com.delta.group.activation.AppActivationClientContext
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppActivationModule {

    @Binds
    @Singleton
    abstract fun bindActivationClientContext(
        impl: AppActivationClientContext,
    ): ActivationClientContext
}

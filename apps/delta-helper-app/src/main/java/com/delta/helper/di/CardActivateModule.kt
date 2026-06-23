package com.delta.helper.di

import com.delta.helper.screen.card.ActivationCheckPort
import com.delta.helper.screen.card.CardActivationPort
import com.delta.helper.screen.card.DevActivationCheckPort
import com.delta.helper.screen.card.RemoteCardActivationPort
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CardActivateModule {

    @Binds
    @Singleton
    abstract fun bindCardActivationPort(
        impl: RemoteCardActivationPort,
    ): CardActivationPort

    @Binds
    @Singleton
    abstract fun bindActivationCheckPort(
        impl: DevActivationCheckPort,
    ): ActivationCheckPort
}

package com.delta.group.activation

import com.delta.core.activation.context.ActivationClientContext
import com.delta.core.activation.context.ActivationClientDefaults
import com.delta.group.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppActivationClientContext @Inject constructor() : ActivationClientContext {
    override val clientOs: String = ActivationClientDefaults.CLIENT_OS_ANDROID
    override val clientChannel: String = BuildConfig.CLIENT_CHANNEL
    override val clientVersionCode: Int? = BuildConfig.VERSION_CODE
    override val publisherKey: String? = BuildConfig.PUBLISHER_KEY
        .trim()
        .takeIf { it.isNotEmpty() }
    override val appPackageName: String = BuildConfig.APPLICATION_ID
}

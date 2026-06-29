package com.delta.helper.activation

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivationGateSupport @Inject constructor(
    private val activationEntranceSupport: ActivationEntranceSupport,
) {
    suspend fun isGateEnabled(): Boolean = activationEntranceSupport.requiresActivation()
}

object ActivationMessages {
    const val ACTIVATED = "内容访问已开通"
}

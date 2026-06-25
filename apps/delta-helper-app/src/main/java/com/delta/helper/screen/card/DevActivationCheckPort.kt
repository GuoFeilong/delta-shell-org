package com.delta.helper.screen.card

import com.delta.helper.activation.HelperActivationStatusStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevActivationCheckPort @Inject constructor(
    private val statusStore: HelperActivationStatusStore,
) : ActivationCheckPort {
    override suspend fun checkActivation(): ActivationCheckResult =
        statusStore.checkActivationForLaunch()
}

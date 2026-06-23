package com.delta.helper.screen.card

import com.delta.core.activation.model.ActivationDeviceStatus
import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.activation.repository.awaitActivationStatus
import com.delta.core.network.model.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteActivationCheckPort @Inject constructor(
    private val activationRepository: ActivationRepository,
) : ActivationCheckPort {
    override suspend fun checkActivation(): ActivationCheckResult =
        when (val result = activationRepository.awaitActivationStatus()) {
            is ApiResult.Success -> {
                val activated = result.data.activated &&
                    result.data.status == ActivationDeviceStatus.ACTIVE
                ActivationCheckResult(
                    activated = activated,
                    message = if (activated) RemoteCardActivationPort.ACTIVATED_MESSAGE else null,
                )
            }
            is ApiResult.Error -> ActivationCheckResult(
                activated = false,
                message = ActivationErrorCodes.messageFor(result.code, result.message),
                isError = true,
            )
            ApiResult.Loading -> ActivationCheckResult(activated = false)
        }
}

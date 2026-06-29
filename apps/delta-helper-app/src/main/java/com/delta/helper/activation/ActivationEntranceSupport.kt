package com.delta.helper.activation

import com.delta.core.activation.model.ActivationEntrance
import com.delta.core.activation.model.ActivationEntrancePath
import com.delta.core.activation.model.ReleaseGate
import com.delta.core.activation.model.UiMode
import com.delta.core.activation.repository.ReleaseGateRepository
import com.delta.core.network.model.ApiResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class ActivationEntranceSupport @Inject constructor(
    private val releaseGateRepository: ReleaseGateRepository,
) {
    suspend fun fetchReleaseGate(): ReleaseGate =
        when (val result = releaseGateRepository.evaluate().first { it !is ApiResult.Loading }) {
            is ApiResult.Success -> result.data
            else -> ReleaseGate(
                uiMode = UiMode.FULL,
                reason = "FALLBACK",
                reviewMode = false,
                entrance = defaultEntrance(),
            )
        }

    suspend fun requiresActivation(): Boolean =
        fetchReleaseGate().uiMode == UiMode.FULL

    suspend fun resolveEntrance(): ActivationEntrance {
        val gate = fetchReleaseGate()
        if (gate.uiMode != UiMode.FULL) {
            return ActivationEntrance(
                cardConfigured = false,
                taskConfigured = false,
                cardVisible = false,
                taskVisible = false,
                defaultPath = null,
                configSource = "TOOL_MODE",
            )
        }
        return gate.entrance ?: defaultEntrance()
    }

    private fun defaultEntrance(): ActivationEntrance = ActivationEntrance(
        cardConfigured = true,
        taskConfigured = true,
        cardVisible = true,
        taskVisible = true,
        defaultPath = null,
        configSource = "DEFAULT",
    )
}

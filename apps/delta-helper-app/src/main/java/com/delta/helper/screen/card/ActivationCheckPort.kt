package com.delta.helper.screen.card

interface ActivationCheckPort {
    suspend fun checkActivation(): ActivationCheckResult
}

data class ActivationCheckResult(
    val activated: Boolean,
    val message: String? = null,
    val isError: Boolean = false,
)

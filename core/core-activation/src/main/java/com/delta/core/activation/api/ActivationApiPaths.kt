package com.delta.core.activation.api

/**
 * Central path registry for activation endpoints.
 *
 * Extension slots at the bottom are reserved for future APIs (release-gate, tasks, etc.).
 * Add matching Retrofit interfaces under [com.delta.core.activation.api.ext] when ready.
 */
object ActivationApiPaths {
    const val STATUS = "api/v1/activation/status"
    const val PURCHASE_URL = "api/v1/activation/cards/purchase-url"
    const val REDEEM = "api/v1/activation/cards/redeem"

    // --- Extension APIs (release-gate, tasks) ---
    const val RELEASE_GATE = "api/v1/activation/release-gate"
    const val TASKS = "api/v1/activation/tasks"
    const val TASK_VERIFY = "api/v1/activation/tasks/{stepId}/verify"
    const val TASK_STEP_REDEEM = "api/v1/activation/tasks/{stepId}/redeem-card"
    const val TASKS_COMPLETE = "api/v1/activation/tasks/complete"
}

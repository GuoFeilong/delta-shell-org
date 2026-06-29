package com.delta.core.activation.model

enum class UiMode {
    TOOL,
    FULL,
    ;

    companion object {
        fun from(raw: String): UiMode =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: FULL
    }
}

data class ReleaseGate(
    val uiMode: UiMode,
    val reason: String,
    val reviewMode: Boolean,
    val entrance: ActivationEntrance? = null,
) {
    val requiresActivation: Boolean
        get() = uiMode == UiMode.FULL
}

data class ActivationEntrance(
    val cardConfigured: Boolean,
    val taskConfigured: Boolean,
    val cardVisible: Boolean,
    val taskVisible: Boolean,
    val defaultPath: ActivationEntrancePath?,
    val configSource: String,
) {
    val hasAnyVisible: Boolean
        get() = cardVisible || taskVisible
}

enum class ActivationEntrancePath {
    CARD,
    TASK,
    ;

    companion object {
        fun from(raw: String?): ActivationEntrancePath? =
            raw?.let { value ->
                entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
    }
}

enum class TaskUnlockMethod {
    ANSWER,
    STEP_CARD,
    ;

    companion object {
        fun from(raw: String?): TaskUnlockMethod? =
            raw?.let { value ->
                entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
    }
}

data class TaskImage(
    val id: Long,
    val imageUrl: String,
    val hints: List<String>,
    val sortOrder: Int,
)

data class TaskStep(
    val id: Long,
    val sortOrder: Int,
    val images: List<TaskImage>,
    val appPackageName: String?,
    val appDownloadUrl: String?,
    val webUrl: String?,
    val copySecret: String?,
    val encourageText: String?,
    val appButtonLabel: String?,
    val webButtonLabel: String?,
    val tutorialVideoUrl: String?,
    val stepCardEnabled: Boolean,
    val stepCardPurchaseUrl: String?,
)

data class TaskList(
    val steps: List<TaskStep>,
    val progressToken: String?,
    val verifiedCount: Int,
    val totalCount: Int,
) {
    val currentStep: TaskStep?
        get() = steps.getOrNull(verifiedCount)
}

data class TaskVerifyResult(
    val correct: Boolean,
    val progressToken: String,
    val verifiedCount: Int,
    val totalCount: Int,
    val completed: Boolean,
    val unlockMethod: TaskUnlockMethod?,
)

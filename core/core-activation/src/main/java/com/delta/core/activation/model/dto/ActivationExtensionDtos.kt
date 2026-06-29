package com.delta.core.activation.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivationEntranceDto(
    val cardConfigured: Boolean = true,
    val taskConfigured: Boolean = true,
    val cardVisible: Boolean = true,
    val taskVisible: Boolean = true,
    val defaultPath: String? = null,
    val configSource: String = "DEFAULT",
)

@Serializable
data class ReleaseGateDto(
    val uiMode: String,
    val reason: String,
    val reviewMode: Boolean,
    val entrance: ActivationEntranceDto? = null,
)

@Serializable
data class TaskImageDto(
    val id: Long,
    val imageUrl: String,
    val hints: List<String> = emptyList(),
    val sortOrder: Int = 0,
)

@Serializable
data class TaskStepDto(
    val id: Long,
    val sortOrder: Int,
    val images: List<TaskImageDto> = emptyList(),
    val appPackageName: String? = null,
    val appDownloadUrl: String? = null,
    val webUrl: String? = null,
    val copySecret: String? = null,
    val encourageText: String? = null,
    val appButtonLabel: String? = null,
    val webButtonLabel: String? = null,
    val tutorialVideoUrl: String? = null,
    val stepCardEnabled: Boolean = false,
    val stepCardPurchaseUrl: String? = null,
)

@Serializable
data class TaskListDto(
    val steps: List<TaskStepDto> = emptyList(),
    val progressToken: String? = null,
    val verifiedCount: Int = 0,
    val totalCount: Int = 0,
)

@Serializable
data class TaskVerifyRequestDto(
    val answer: String,
)

@Serializable
data class TaskVerifyResultDto(
    val correct: Boolean,
    val progressToken: String,
    val verifiedCount: Int,
    val totalCount: Int,
    val completed: Boolean,
    val unlockMethod: String? = null,
)

@Serializable
data class TaskStepCardRedeemRequestDto(
    val cardCode: String,
)

@Serializable
data class TaskCompleteRequestDto(
    val progressToken: String,
)

package com.delta.core.activation.model

import com.delta.core.activation.model.dto.ActivationEntranceDto
import com.delta.core.activation.model.dto.ReleaseGateDto
import com.delta.core.activation.model.dto.TaskImageDto
import com.delta.core.activation.model.dto.TaskListDto
import com.delta.core.activation.model.dto.TaskStepDto
import com.delta.core.activation.model.dto.TaskVerifyResultDto

fun ReleaseGateDto.toDomain(): ReleaseGate = ReleaseGate(
    uiMode = UiMode.from(uiMode),
    reason = reason,
    reviewMode = reviewMode,
    entrance = entrance?.toDomain(),
)

fun ActivationEntranceDto.toDomain(): ActivationEntrance = ActivationEntrance(
    cardConfigured = cardConfigured,
    taskConfigured = taskConfigured,
    cardVisible = cardVisible,
    taskVisible = taskVisible,
    defaultPath = ActivationEntrancePath.from(defaultPath),
    configSource = configSource,
)

fun TaskListDto.toDomain(): TaskList = TaskList(
    steps = steps.map { it.toDomain() },
    progressToken = progressToken,
    verifiedCount = verifiedCount,
    totalCount = totalCount,
)

fun TaskStepDto.toDomain(): TaskStep = TaskStep(
    id = id,
    sortOrder = sortOrder,
    images = images.map { it.toDomain() },
    appPackageName = appPackageName,
    appDownloadUrl = appDownloadUrl,
    webUrl = webUrl,
    copySecret = copySecret,
    encourageText = encourageText,
    appButtonLabel = appButtonLabel,
    webButtonLabel = webButtonLabel,
    tutorialVideoUrl = tutorialVideoUrl,
    stepCardEnabled = stepCardEnabled,
    stepCardPurchaseUrl = stepCardPurchaseUrl,
)

fun TaskImageDto.toDomain(): TaskImage = TaskImage(
    id = id,
    imageUrl = imageUrl,
    hints = hints,
    sortOrder = sortOrder,
)

fun TaskVerifyResultDto.toDomain(): TaskVerifyResult = TaskVerifyResult(
    correct = correct,
    progressToken = progressToken,
    verifiedCount = verifiedCount,
    totalCount = totalCount,
    completed = completed,
    unlockMethod = TaskUnlockMethod.from(unlockMethod),
)

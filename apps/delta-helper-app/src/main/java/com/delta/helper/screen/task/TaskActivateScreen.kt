package com.delta.helper.screen.task

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.delta.core.activation.model.TaskImage
import com.delta.core.activation.model.TaskStep
import com.delta.helper.screen.component.HzCardInputField
import com.delta.helper.screen.component.HzPrimaryButton
import com.delta.helper.screen.component.HzSecondaryButton
import com.delta.helper.screen.component.HzSnackbarMessageEffect
import com.delta.helper.screen.component.HzSnackbarType
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.component.LocalHzSnackbarHostState
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.screen.promo.WechatGuideCopy
import com.delta.helper.ui.theme.HzColors

@Composable
fun TaskActivateRoute(
    viewModelStoreKey: Int,
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: TaskActivateViewModel = hiltViewModel(key = "task_activate_$viewModelStoreKey"),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = LocalHzSnackbarHostState.current
    val spec = rememberHelperAdaptiveSpec()
    var handledActivationSuccessToken by remember(viewModelStoreKey) { mutableStateOf(0L) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.loadTasks()
    }

    LaunchedEffect(
        uiState.activationSuccessToken,
        uiState.isSubmitting,
    ) {
        if (uiState.activationSuccessToken > handledActivationSuccessToken && uiState.isActivated) {
            handledActivationSuccessToken = uiState.activationSuccessToken
            onActivated()
        }
    }

    BackHandler(onBack = onBack)

    LaunchedEffect(viewModel) {
        viewModel.openUrl.collect { url ->
            val browserOpened = openExternalLink(context, url)
            snackbarHostState.showMessage(
                message = WechatGuideCopy.purchaseOpenedMessage(browserOpened),
                type = HzSnackbarType.Success,
            )
        }
    }

    HzSnackbarMessageEffect(
        message = uiState.errorMessage,
        type = HzSnackbarType.Error,
        onConsumed = viewModel::clearErrorMessage,
    )

    HzSnackbarMessageEffect(
        message = uiState.successMessage,
        type = HzSnackbarType.Success,
        onConsumed = viewModel::clearSuccessMessage,
    )

    previewImageUrl?.let { url ->
        TaskImagePreviewDialog(
            imageUrl = url,
            onDismiss = { previewImageUrl = null },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        HelperHomeBackground()
        HelperAdaptiveContainer(
            modifier = Modifier.fillMaxSize(),
            spec = spec,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
            ) {
                HzTopBar(
                    title = TaskActivateCopy.PAGE_TITLE,
                    subtitle = TaskActivateCopy.PAGE_SUBTITLE,
                    showBack = true,
                    onBack = onBack,
                )

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = HzColors.Primary)
                        }
                    }

                    uiState.currentStep != null -> {
                        TaskActivateContent(
                            uiState = uiState,
                            onAnswerChanged = viewModel::onAnswerChanged,
                            onStepCardCodeChanged = viewModel::onStepCardCodeChanged,
                            onSubmit = viewModel::submitAnswer,
                            onRedeemStepCard = viewModel::redeemStepCard,
                            onOpenLink = viewModel::openLink,
                            onPreviewImage = { previewImageUrl = it },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = uiState.errorMessage ?: TaskActivateCopy.EMPTY_TASKS,
                                color = HzColors.TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HzSecondaryButton(
                                text = TaskActivateCopy.RELOAD_TASKS,
                                onClick = viewModel::loadTasks,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskActivateContent(
    uiState: TaskActivateUiState,
    onAnswerChanged: (String) -> Unit,
    onStepCardCodeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onRedeemStepCard: () -> Unit,
    onOpenLink: (String?) -> Unit,
    onPreviewImage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val step = uiState.currentStep ?: return
    val images = step.images.sortedBy { it.sortOrder }

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TaskProgressCard(uiState = uiState)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HzColors.BgCard)
                    .border(1.dp, HzColors.Border, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                images.forEachIndexed { index, image ->
                    if (index > 0) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(HzColors.Border.copy(alpha = 0.6f)),
                        )
                    }
                    TaskTutorialImageBlock(
                        image = image,
                        onPreviewImage = onPreviewImage,
                    )
                }

                TaskStepActionRow(
                    step = step,
                    onOpenLink = onOpenLink,
                    onCopySecret = { copyToClipboard(context, it) },
                    onOpenApp = { packageName ->
                        openAppByPackage(context, packageName, step.appDownloadUrl, onOpenLink)
                    },
                )
            }
        }

        TaskAnswerFooter(
            answer = uiState.answer,
            stepCardCode = uiState.stepCardCode,
            step = step,
            isSubmitting = uiState.isSubmitting,
            canSubmit = uiState.canSubmit,
            canRedeemStepCard = uiState.canRedeemStepCard,
            onAnswerChanged = onAnswerChanged,
            onStepCardCodeChanged = onStepCardCodeChanged,
            onSubmit = onSubmit,
            onRedeemStepCard = onRedeemStepCard,
            onOpenLink = onOpenLink,
        )
    }
}

@Composable
private fun TaskTutorialImageBlock(
    image: TaskImage,
    onPreviewImage: (String) -> Unit,
) {
    val hints = image.hints.filter { it.isNotBlank() }
    if (hints.isNotEmpty()) {
        Text(
            text = TaskActivateCopy.IMAGE_HINT_LABEL,
            style = MaterialTheme.typography.labelMedium,
            color = HzColors.Primary,
        )
        hints.forEach { hint ->
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = HzColors.TextPrimary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
            )
        }
    }

    if (image.imageUrl.isBlank()) return

    SubcomposeAsyncImage(
        model = image.imageUrl,
        contentDescription = TaskActivateCopy.VIEW_TUTORIAL_IMAGE,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, HzColors.Border, RoundedCornerShape(12.dp))
            .clickable { onPreviewImage(image.imageUrl) },
        contentScale = ContentScale.FillWidth,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(HzColors.BgInput),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = HzColors.Primary,
                    strokeWidth = 2.dp,
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(HzColors.BgInput)
                    .clickable { onPreviewImage(image.imageUrl) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = TaskActivateCopy.IMAGE_LOAD_FAILED,
                    style = MaterialTheme.typography.bodySmall,
                    color = HzColors.TextMuted,
                )
            }
        },
    )

    Text(
        text = TaskActivateCopy.TAP_TO_PREVIEW,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelSmall,
        color = HzColors.TextMuted,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TaskStepActionRow(
    step: TaskStep,
    onOpenLink: (String?) -> Unit,
    onCopySecret: (String) -> Unit,
    onOpenApp: (String) -> Unit,
) {
    val hasActions = !step.appPackageName.isNullOrBlank() ||
        !step.webUrl.isNullOrBlank() ||
        !step.copySecret.isNullOrBlank() ||
        !step.tutorialVideoUrl.isNullOrBlank()
    if (!hasActions) return

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HzColors.Border.copy(alpha = 0.6f)),
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        step.appPackageName?.takeIf { it.isNotBlank() }?.let { pkg ->
            TaskActionChip(
                text = step.appButtonLabel ?: "打开APP",
                onClick = { onOpenApp(pkg) },
            )
        }
        step.webUrl?.takeIf { it.isNotBlank() }?.let { url ->
            TaskActionChip(
                text = step.webButtonLabel ?: "打开链接",
                onClick = { onOpenLink(url) },
            )
        }
        step.copySecret?.takeIf { it.isNotBlank() }?.let { secret ->
            TaskActionChip(
                text = TaskActivateCopy.COPY_SECRET,
                onClick = { onCopySecret(secret) },
            )
        }
        step.tutorialVideoUrl?.takeIf { it.isNotBlank() }?.let { url ->
            TaskActionChip(
                text = TaskActivateCopy.OPEN_VIDEO,
                onClick = { onOpenLink(url) },
            )
        }
    }
}

@Composable
private fun TaskActionChip(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(HzColors.BgInput)
            .border(1.dp, HzColors.Primary.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelMedium,
        color = HzColors.Primary,
    )
}

@Composable
private fun TaskImagePreviewDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
) {
    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HzColors.BgPrimary.copy(alpha = 0.96f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = HzColors.TextPrimary,
                )
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = TaskActivateCopy.VIEW_TUTORIAL_IMAGE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 56.dp)
                    .clickable(onClick = onDismiss),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun TaskProgressCard(uiState: TaskActivateUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HzColors.BgCard)
            .border(1.dp, HzColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = TaskActivateCopy.PROGRESS_LABEL,
                style = MaterialTheme.typography.labelLarge,
                color = HzColors.TextSecondary,
            )
            Text(
                text = "${uiState.progressCurrent} / ${uiState.totalCount.coerceAtLeast(1)}",
                style = MaterialTheme.typography.titleMedium,
                color = HzColors.Primary,
            )
        }
        LinearProgressIndicator(
            progress = { uiState.progressPercent },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = HzColors.Primary,
            trackColor = HzColors.BgInput,
        )
        Text(
            text = uiState.heartenText,
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextMuted,
        )
    }
}

@Composable
private fun TaskAnswerFooter(
    answer: String,
    stepCardCode: String,
    step: TaskStep,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    canRedeemStepCard: Boolean,
    onAnswerChanged: (String) -> Unit,
    onStepCardCodeChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onRedeemStepCard: () -> Unit,
    onOpenLink: (String?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HzColors.BgSecondary)
            .border(
                width = 1.dp,
                color = HzColors.Border,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HzCardInputField(
            value = answer,
            onValueChange = onAnswerChanged,
            placeholder = TaskActivateCopy.ANSWER_PLACEHOLDER,
            enabled = !isSubmitting,
        )
        HzPrimaryButton(
            text = if (isSubmitting) TaskActivateCopy.SUBMITTING_BUTTON else TaskActivateCopy.SUBMIT_BUTTON,
            onClick = onSubmit,
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth(),
        )

        if (step.stepCardEnabled) {
            HzCardInputField(
                value = stepCardCode,
                onValueChange = onStepCardCodeChanged,
                placeholder = TaskActivateCopy.STEP_CARD_PLACEHOLDER,
                enabled = !isSubmitting,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HzPrimaryButton(
                    text = TaskActivateCopy.STEP_CARD_REDEEM,
                    onClick = onRedeemStepCard,
                    enabled = canRedeemStepCard,
                    modifier = Modifier.weight(1f),
                )
                HzSecondaryButton(
                    text = TaskActivateCopy.STEP_CARD_PURCHASE,
                    onClick = { onOpenLink(step.stepCardPurchaseUrl) },
                    enabled = !step.stepCardPurchaseUrl.isNullOrBlank(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun openExternalLink(context: Context, url: String): Boolean {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("external_link", url))
    return runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
        true
    }.getOrDefault(false)
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("task_secret", text))
}

private fun openAppByPackage(
    context: Context,
    packageName: String,
    downloadUrl: String?,
    onOpenLink: (String?) -> Unit,
) {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(packageName)
    if (launchIntent != null) {
        context.startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } else if (!downloadUrl.isNullOrBlank()) {
        onOpenLink(downloadUrl)
    } else {
        runCatching {
            pm.getPackageInfo(packageName, 0)
        }.onFailure {
            onOpenLink("market://details?id=$packageName")
        }
    }
}

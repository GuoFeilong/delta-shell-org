package com.delta.features.activation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.core.activation.model.TaskStep

@Composable
fun TaskActivationRoute(
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    viewModel: TaskActivationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.loadTasks()
    }

    LaunchedEffect(viewModel) {
        viewModel.openUrl.collect { url ->
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
            )
        }
    }

    if (uiState.isActivated) {
        LaunchedEffect(Unit) { onActivated() }
    }

    TaskActivationScreen(
        uiState = uiState,
        onAnswerChange = viewModel::onAnswerChanged,
        onStepCardCodeChange = viewModel::onStepCardCodeChanged,
        onVerifyClick = viewModel::verifyCurrentStep,
        onRedeemStepCardClick = viewModel::redeemStepCard,
        onOpenLink = viewModel::openLink,
        onReloadClick = viewModel::loadTasks,
        modifier = modifier,
    )
}

@Composable
fun TaskActivationScreen(
    uiState: TaskActivationUiState,
    onAnswerChange: (String) -> Unit,
    onStepCardCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onRedeemStepCardClick: () -> Unit,
    onOpenLink: (String?) -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "任务激活",
            style = MaterialTheme.typography.titleLarge,
        )

        uiState.feedbackMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        when {
            uiState.isLoading -> {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            }
            uiState.currentStep == null && uiState.totalCount == 0 -> {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = onReloadClick) {
                    Text("重新加载任务")
                }
            }
            uiState.currentStep != null -> {
                Spacer(modifier = Modifier.height(16.dp))
                TaskStepContent(
                    step = uiState.currentStep,
                    answer = uiState.answer,
                    stepCardCode = uiState.stepCardCode,
                    isSubmitting = uiState.isSubmitting,
                    canSubmitAnswer = uiState.canSubmitAnswer,
                    canRedeemStepCard = uiState.canRedeemStepCard,
                    onAnswerChange = onAnswerChange,
                    onStepCardCodeChange = onStepCardCodeChange,
                    onVerifyClick = onVerifyClick,
                    onRedeemStepCardClick = onRedeemStepCardClick,
                    onOpenLink = onOpenLink,
                )
            }
        }

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private enum class TaskStepFooterMode {
    Answer,
    StepCard,
}

@Composable
private fun TaskStepContent(
    step: TaskStep,
    answer: String,
    stepCardCode: String,
    isSubmitting: Boolean,
    canSubmitAnswer: Boolean,
    canRedeemStepCard: Boolean,
    onAnswerChange: (String) -> Unit,
    onStepCardCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onRedeemStepCardClick: () -> Unit,
    onOpenLink: (String?) -> Unit,
) {
    var footerMode by remember(step.id) { mutableStateOf(TaskStepFooterMode.Answer) }

    step.encourageText?.takeIf { it.isNotBlank() }?.let { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    step.images.forEach { image ->
        image.hints.filter { it.isNotBlank() }.forEach { hint ->
            Text(
                text = "• $hint",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (image.imageUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = { onOpenLink(image.imageUrl) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("查看步骤图片")
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    step.appDownloadUrl?.takeIf { it.isNotBlank() }?.let { url ->
        OutlinedButton(
            onClick = { onOpenLink(url) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(step.appButtonLabel ?: "下载应用")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    step.webUrl?.takeIf { it.isNotBlank() }?.let { url ->
        OutlinedButton(
            onClick = { onOpenLink(url) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(step.webButtonLabel ?: "打开网页")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    step.tutorialVideoUrl?.takeIf { it.isNotBlank() }?.let { url ->
        OutlinedButton(
            onClick = { onOpenLink(url) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("观看教程")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    step.copySecret?.takeIf { it.isNotBlank() }?.let { secret ->
        Text(
            text = "提示：$secret",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (step.stepCardEnabled) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = footerMode == TaskStepFooterMode.Answer,
                onClick = { footerMode = TaskStepFooterMode.Answer },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text("输入答案")
            }
            SegmentedButton(
                selected = footerMode == TaskStepFooterMode.StepCard,
                onClick = { footerMode = TaskStepFooterMode.StepCard },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text("步骤卡密")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (!step.stepCardEnabled || footerMode == TaskStepFooterMode.Answer) {
        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("步骤答案") },
            singleLine = true,
            enabled = !isSubmitting,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onVerifyClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = canSubmitAnswer,
        ) {
            Text(if (isSubmitting) "提交中…" else "提交答案")
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = stepCardCode,
                onValueChange = onStepCardCodeChange,
                modifier = Modifier.weight(1f),
                label = { Text("步骤卡密") },
                placeholder = { Text("输入步骤卡密") },
                singleLine = true,
                enabled = !isSubmitting,
            )
            Button(
                onClick = onRedeemStepCardClick,
                enabled = canRedeemStepCard,
            ) {
                Text("解锁")
            }
        }
        if (!step.stepCardPurchaseUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "没有卡密？去购买",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenLink(step.stepCardPurchaseUrl) },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (isSubmitting) {
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator()
    }
}

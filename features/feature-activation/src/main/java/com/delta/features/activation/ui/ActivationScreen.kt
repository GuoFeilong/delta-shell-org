package com.delta.features.activation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ActivationRoute(
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    viewModel: ActivationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isActivated) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            onActivated()
        }
    }

    ActivationScreen(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onCardCodeChange = viewModel::onCardCodeChanged,
        onRedeemClick = viewModel::redeemCard,
        onPurchaseClick = viewModel::openPurchaseUrl,
        onRetryClick = viewModel::refreshStatus,
        onTaskActivated = viewModel::markActivated,
        modifier = modifier,
    )
}

@Composable
fun ActivationGate(
    modifier: Modifier = Modifier,
    viewModel: ActivationViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isChecking -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.isActivated -> content()
        else -> {
            ActivationScreen(
                uiState = uiState,
                onTabSelected = viewModel::onTabSelected,
                onCardCodeChange = viewModel::onCardCodeChanged,
                onRedeemClick = viewModel::redeemCard,
                onPurchaseClick = viewModel::openPurchaseUrl,
                onRetryClick = viewModel::refreshStatus,
                onTaskActivated = viewModel::markActivated,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun ActivationScreen(
    uiState: ActivationUiState,
    onTabSelected: (ActivationTab) -> Unit,
    onCardCodeChange: (String) -> Unit,
    onRedeemClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onRetryClick: () -> Unit,
    onTaskActivated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "应用激活",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.statusMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        uiState.releaseGate?.let { gate ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Release Gate: ${gate.uiMode.name} (${gate.reason})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = uiState.selectedTab == ActivationTab.CARD,
                onClick = { onTabSelected(ActivationTab.CARD) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text("卡密激活")
            }
            SegmentedButton(
                selected = uiState.selectedTab == ActivationTab.TASK,
                onClick = { onTabSelected(ActivationTab.TASK) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text("任务激活")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState.selectedTab) {
            ActivationTab.CARD -> CardActivationContent(
                uiState = uiState,
                onCardCodeChange = onCardCodeChange,
                onRedeemClick = onRedeemClick,
                onPurchaseClick = onPurchaseClick,
                onRetryClick = onRetryClick,
            )
            ActivationTab.TASK -> TaskActivationRoute(
                onActivated = onTaskActivated,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CardActivationContent(
    uiState: ActivationUiState,
    onCardCodeChange: (String) -> Unit,
    onRedeemClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    if (uiState.isBusy && uiState.selectedTab == ActivationTab.CARD) {
        CircularProgressIndicator()
        return
    }

    OutlinedTextField(
        value = uiState.cardCode,
        onValueChange = onCardCodeChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("卡密") },
        singleLine = true,
        enabled = !uiState.isActivated,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onRedeemClick() }),
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onRedeemClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = uiState.canRedeem,
    ) {
        Text("激活")
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
        onClick = onPurchaseClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = uiState.canOpenPurchaseUrl,
    ) {
        Text(if (uiState.purchaseUrl.isNullOrBlank()) "获取购卡链接" else "前往购卡")
    }

    if (uiState.errorMessage != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = uiState.errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }

    if (uiState.canRetry) {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onRetryClick) {
            Text("重新检查激活状态")
        }
    }
}

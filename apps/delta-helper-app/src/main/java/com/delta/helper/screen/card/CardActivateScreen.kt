package com.delta.helper.screen.card

import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.helper.screen.component.ActivationRetentionDialog
import com.delta.helper.screen.component.HzCardInputField
import com.delta.helper.screen.component.HzConfirmDialog
import com.delta.helper.screen.component.HzLegalAgreementRow
import com.delta.helper.screen.component.HzPlainAckRow
import com.delta.helper.screen.component.HzPlanPicker
import com.delta.helper.screen.component.HzPrimaryButton
import com.delta.helper.screen.component.HzSnackbarMessageEffect
import com.delta.helper.screen.component.HzSnackbarType
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.component.LocalHzSnackbarHostState
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.HelperAdaptiveSpec
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.screen.legal.LegalCopy
import com.delta.helper.screen.legal.LegalDocType
import com.delta.helper.screen.legal.LegalDocumentScreen
import com.delta.helper.screen.promo.WechatGuideCopy
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CardActivateRoute(
    viewModelStoreKey: Int,
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: CardActivateViewModel = hiltViewModel(key = "card_activate_$viewModelStoreKey"),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = LocalHzSnackbarHostState.current
    val spec = rememberHelperAdaptiveSpec()
    var legalDoc by rememberSaveable { mutableStateOf<LegalDocType?>(null) }
    var handledActivationSuccessToken by remember(viewModelStoreKey) { mutableStateOf(0L) }
    var showActivationRetentionDialog by remember(viewModelStoreKey) { mutableStateOf(false) }

    LaunchedEffect(
        uiState.activationSuccessToken,
        uiState.isActivating,
        uiState.showActivateConfirm,
    ) {
        val hasNewSuccessfulActivation =
            uiState.activationSuccessToken > handledActivationSuccessToken &&
                uiState.successMessage != null
        if (hasNewSuccessfulActivation && !uiState.isActivating && !uiState.showActivateConfirm) {
            handledActivationSuccessToken = uiState.activationSuccessToken
            showActivationRetentionDialog = true
            viewModel.clearSuccessMessage()
        }
    }

    BackHandler(enabled = !showActivationRetentionDialog) {
        when {
            legalDoc != null -> legalDoc = null
            else -> onBack()
        }
    }

    if (showActivationRetentionDialog) {
        ActivationRetentionDialog(
            onDismiss = {
                showActivationRetentionDialog = false
                onActivated()
            },
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.openPurchaseUrl.collect { url ->
            val browserOpened = copyPurchaseLinkAndOpenBrowser(context, url)
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

    legalDoc?.let { docType ->
        LegalDocumentScreen(
            initialType = docType,
            onBack = { legalDoc = null },
            modifier = modifier,
            spec = spec,
        )
        return
    }

    if (uiState.showActivateConfirm) {
        HzConfirmDialog(
            title = LegalCopy.CARD_ACTIVATE_CONFIRM_TITLE,
            message = LegalCopy.CARD_ACTIVATE_CONFIRM_CONTENT,
            confirmText = "继续激活",
            dismissText = "取消",
            onConfirm = viewModel::confirmActivate,
            onDismiss = viewModel::dismissActivateConfirm,
        )
    }

    uiState.purchaseConfirmUrl?.let {
        HzConfirmDialog(
            title = LegalCopy.PURCHASE_CONFIRM_TITLE,
            message = LegalCopy.purchaseConfirmContent(uiState.selectedPlanTitle),
            confirmText = "已知悉，去购买",
            dismissText = "取消",
            onConfirm = viewModel::confirmPurchase,
            onDismiss = viewModel::dismissPurchaseConfirm,
        )
    }

    CardActivateScreen(
        uiState = uiState,
        onCardCodeChange = viewModel::onCardCodeChanged,
        onLegalCheckedChange = viewModel::onLegalCheckedChanged,
        onServiceNatureAckChange = viewModel::onServiceNatureAckChanged,
        onPlanSelected = viewModel::onPlanSelected,
        onAccessModeChanged = viewModel::onAccessModeChanged,
        onTipsExpandedChange = viewModel::onTipsExpandedChanged,
        onOpenLegal = { legalDoc = it },
        onActivateClick = viewModel::activate,
        onPurchaseClick = viewModel::purchaseCard,
        onBack = onBack,
        modifier = modifier,
        spec = spec,
    )
}

private fun copyPurchaseLinkAndOpenBrowser(context: Context, url: String): Boolean {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("purchase_url", url))

    return runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
        true
    }.getOrDefault(false)
}

@Composable
fun CardActivateScreen(
    uiState: CardActivateUiState,
    onCardCodeChange: (String) -> Unit,
    onLegalCheckedChange: (Boolean) -> Unit,
    onServiceNatureAckChange: (Boolean) -> Unit,
    onPlanSelected: (Int) -> Unit,
    onAccessModeChanged: (CardActivateAccessMode) -> Unit,
    onTipsExpandedChange: (Boolean) -> Unit,
    onOpenLegal: (LegalDocType) -> Unit,
    onActivateClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    spec: HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(60.milliseconds)
        visible = true
    }

    val effectiveMode = when {
        uiState.hasPurchaseOptions -> uiState.accessMode
        else -> CardActivateAccessMode.ACTIVATE
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        HelperHomeBackground()

        HelperAdaptiveContainer(
            modifier = Modifier.fillMaxSize(),
            spec = spec,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = if (spec.isTabletOrFoldExpanded) 24.dp else 8.dp,
                        bottom = 28.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HzTopBar(
                    title = CardActivateCopy.PAGE_TITLE,
                    subtitle = CardActivateCopy.PAGE_SUBTITLE,
                    showBack = true,
                    onBack = onBack,
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 8 },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (uiState.hasPurchaseOptions) {
                            CardActivateSegmentTabs(
                                selectedMode = effectiveMode,
                                onModeSelected = onAccessModeChanged,
                            )
                        }

                        CardActivateGlassPanel {
                            AnimatedContent(
                                targetState = effectiveMode,
                                transitionSpec = {
                                    fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                                },
                                label = "cardAccessMode",
                            ) { mode ->
                                when (mode) {
                                    CardActivateAccessMode.PURCHASE -> CardActivatePurchaseSection(
                                        uiState = uiState,
                                        onPlanSelected = onPlanSelected,
                                        onPurchaseClick = onPurchaseClick,
                                        onOpenLegal = onOpenLegal,
                                    )
                                    CardActivateAccessMode.ACTIVATE -> CardActivateActivateSection(
                                        uiState = uiState,
                                        onCardCodeChange = onCardCodeChange,
                                        onLegalCheckedChange = onLegalCheckedChange,
                                        onServiceNatureAckChange = onServiceNatureAckChange,
                                        onOpenLegal = onOpenLegal,
                                        onActivateClick = onActivateClick,
                                    )
                                }
                            }
                        }

                        CardActivateTipsPanel(
                            expanded = uiState.tipsExpanded,
                            onExpandedChange = onTipsExpandedChange,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardActivatePurchaseSection(
    uiState: CardActivateUiState,
    onPlanSelected: (Int) -> Unit,
    onPurchaseClick: () -> Unit,
    onOpenLegal: (LegalDocType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = CardActivateCopy.PRODUCT_NATURE_SHORT,
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextSecondary,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
        )

        HzPlanPicker(
            options = uiState.purchaseOptions,
            selectedIndex = uiState.selectedPlanIndex,
            onSelected = onPlanSelected,
            embedded = true,
        )

        uiState.selectedPlanHint?.let { hint ->
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextMuted,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        HzPrimaryButton(
            text = uiState.purchaseButtonText,
            onClick = onPurchaseClick,
            enabled = uiState.canPurchase,
        )

        CardActivatePurchaseLegalNote(
            onOpenPaymentDoc = { onOpenLegal(LegalDocType.Payment) },
        )
    }
}

@Composable
private fun CardActivateActivateSection(
    uiState: CardActivateUiState,
    onCardCodeChange: (String) -> Unit,
    onLegalCheckedChange: (Boolean) -> Unit,
    onServiceNatureAckChange: (Boolean) -> Unit,
    onOpenLegal: (LegalDocType) -> Unit,
    onActivateClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CardActivateNoticeStrip(text = CardActivateCopy.PRODUCT_NATURE_SHORT)

        HzCardInputField(
            value = uiState.cardCode,
            onValueChange = onCardCodeChange,
            placeholder = CardActivateCopy.INPUT_PLACEHOLDER,
            enabled = !uiState.isActivating,
        )

        HzLegalAgreementRow(
            checked = uiState.legalAccepted,
            onCheckedChange = onLegalCheckedChange,
            onOpenLegal = onOpenLegal,
        )

        HzPlainAckRow(
            checked = uiState.serviceNatureAcknowledged,
            onCheckedChange = onServiceNatureAckChange,
            text = CardActivateCopy.SERVICE_NATURE_ACK,
        )

        HzPrimaryButton(
            text = if (uiState.isActivating) {
                CardActivateCopy.ACTIVATING_BUTTON
            } else {
                CardActivateCopy.ACTIVATE_BUTTON
            },
            onClick = onActivateClick,
            enabled = uiState.canActivate,
        )
    }
}

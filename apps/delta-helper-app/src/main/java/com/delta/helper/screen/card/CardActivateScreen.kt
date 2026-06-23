package com.delta.helper.screen.card

import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.delta.helper.screen.component.HzCardInputField
import com.delta.helper.screen.component.HzConfirmDialog
import com.delta.helper.screen.component.HzInlineMessage
import com.delta.helper.screen.component.HzLegalAgreementRow
import com.delta.helper.screen.component.HzPlainAckRow
import com.delta.helper.screen.component.HzPrimaryButton
import com.delta.helper.screen.component.HzSecondaryButton
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.screen.legal.LegalCopy
import com.delta.helper.screen.legal.LegalDocType
import com.delta.helper.screen.legal.LegalDocumentScreen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CardActivateRoute(
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: CardActivateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val spec = rememberHelperAdaptiveSpec()
    var legalDoc by rememberSaveable { mutableStateOf<LegalDocType?>(null) }

    BackHandler(enabled = legalDoc == null) { onBack() }

    LaunchedEffect(viewModel) {
        viewModel.openPurchaseUrl.collect { url ->
            copyPurchaseLinkAndOpenBrowser(context, url)
        }
    }

    if (uiState.isActivated) {
        LaunchedEffect(Unit) { onActivated() }
    }

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
            message = LegalCopy.PURCHASE_CONFIRM_CONTENT,
            confirmText = "已知悉",
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
        onOpenLegal = { legalDoc = it },
        onActivateClick = viewModel::activate,
        onPurchaseClick = viewModel::purchaseCard,
        onBack = onBack,
        modifier = modifier,
        spec = spec,
    )
}

private fun copyPurchaseLinkAndOpenBrowser(context: Context, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("purchase_url", url))

    val browserOpened = runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
        true
    }.getOrDefault(false)

    val message = if (browserOpened) {
        "链接已复制，购买页已打开"
    } else {
        "链接已复制，请在浏览器打开"
    }
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun CardActivateScreen(
    uiState: CardActivateUiState,
    onCardCodeChange: (String) -> Unit,
    onLegalCheckedChange: (Boolean) -> Unit,
    onServiceNatureAckChange: (Boolean) -> Unit,
    onOpenLegal: (LegalDocType) -> Unit,
    onActivateClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    spec: com.delta.helper.screen.layout.HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(60.milliseconds)
        visible = true
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
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                HzTopBar(
                    title = CardActivateCopy.PAGE_TITLE,
                    subtitle = CardActivateCopy.PAGE_SUBTITLE,
                    showBack = true,
                    onBack = onBack,
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 5 },
                ) {
                    CardActivateHero()
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 80)) +
                        slideInVertically(tween(500, delayMillis = 80)) { it / 4 },
                ) {
                    CardActivateGlassPanel {
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

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                HzPrimaryButton(
                                    text = if (uiState.isActivating) {
                                        CardActivateCopy.ACTIVATING_BUTTON
                                    } else {
                                        CardActivateCopy.ACTIVATE_BUTTON
                                    },
                                    onClick = onActivateClick,
                                    enabled = uiState.canActivate,
                                )

                                HzSecondaryButton(
                                    text = CardActivateCopy.PURCHASE_BUTTON,
                                    onClick = onPurchaseClick,
                                    enabled = !uiState.isActivating,
                                )
                            }

                            uiState.errorMessage?.let { message ->
                                HzInlineMessage(message = message, isError = true)
                            }

                            uiState.successMessage?.let { message ->
                                HzInlineMessage(message = message, isError = false)
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(550, delayMillis = 160)),
                ) {
                    CardActivateTipsCard(text = CardActivateCopy.TIPS_TEXT)
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

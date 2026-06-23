package com.delta.helper.screen.card

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import com.delta.helper.screen.component.HzHeroSection
import com.delta.helper.screen.component.HzInlineMessage
import com.delta.helper.screen.component.HzLegalAgreementRow
import com.delta.helper.screen.component.HzNoticeBanner
import com.delta.helper.screen.component.HzPrimaryButton
import com.delta.helper.screen.component.HzSecondaryButton
import com.delta.helper.screen.component.HzTipsCard
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.screen.legal.LegalCopy
import com.delta.helper.screen.legal.LegalDocType
import com.delta.helper.screen.legal.LegalDocumentScreen
import com.delta.helper.ui.theme.HzColors

@Composable
fun CardActivateRoute(
    modifier: Modifier = Modifier,
    onActivated: () -> Unit = {},
    viewModel: CardActivateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val spec = rememberHelperAdaptiveSpec()
    var legalDoc by rememberSaveable { mutableStateOf<LegalDocType?>(null) }

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
        onOpenLegal = { legalDoc = it },
        onActivateClick = viewModel::activate,
        onPurchaseClick = viewModel::purchaseCard,
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
    onOpenLegal: (LegalDocType) -> Unit,
    onActivateClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    modifier: Modifier = Modifier,
    spec: com.delta.helper.screen.layout.HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
) {
    HelperAdaptiveContainer(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        spec = spec,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = if (spec.isTabletOrFoldExpanded) 24.dp else 8.dp,
                    bottom = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HzTopBar(
                title = CardActivateCopy.PAGE_TITLE,
                subtitle = CardActivateCopy.PAGE_SUBTITLE,
            )

            HzNoticeBanner(text = CardActivateCopy.PRODUCT_NATURE_SHORT)

            HzHeroSection(
                title = CardActivateCopy.PAGE_TITLE,
                description = CardActivateCopy.PAGE_DESC,
                modifier = Modifier.padding(vertical = if (spec.isTabletOrFoldExpanded) 8.dp else 0.dp),
            )

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

                if (!uiState.purchaseUrl.isNullOrBlank()) {
                    HzSecondaryButton(
                        text = CardActivateCopy.PURCHASE_BUTTON,
                        onClick = onPurchaseClick,
                        enabled = !uiState.isActivating,
                    )
                } else {
                    HzNoticeBanner(
                        text = CardActivateCopy.NO_PURCHASE_LINK,
                        backgroundColor = HzColors.BgElevated.copy(alpha = 0.35f),
                        borderColor = HzColors.Border,
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                HzInlineMessage(message = message, isError = true)
            }

            uiState.successMessage?.let { message ->
                HzInlineMessage(message = message, isError = false)
            }

            Spacer(modifier = Modifier.height(8.dp))

            HzTipsCard(text = CardActivateCopy.TIPS_TEXT)
        }
    }
}

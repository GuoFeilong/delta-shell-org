package com.delta.helper.screen.consent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.helper.screen.component.HzLegalAgreementRow
import com.delta.helper.screen.component.HzPlainAckRow
import com.delta.helper.screen.component.HzPrimaryButton
import com.delta.helper.screen.component.HzSecondaryButton
import com.delta.helper.screen.component.HzSnackbarMessageEffect
import com.delta.helper.screen.component.HzSnackbarType
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.screen.legal.LegalCopy
import com.delta.helper.screen.legal.LegalDocType
import com.delta.helper.screen.legal.LegalDocumentScreen
import com.delta.helper.ui.theme.HzColors

@Composable
fun FirstLaunchConsentRoute(
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FirstLaunchConsentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spec = rememberHelperAdaptiveSpec()
    var legalDoc by rememberSaveable { mutableStateOf<LegalDocType?>(null) }

    BackHandler(enabled = legalDoc == null) {
        onDecline()
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

    HzSnackbarMessageEffect(
        message = uiState.errorMessage,
        type = HzSnackbarType.Error,
        onConsumed = viewModel::clearErrorMessage,
    )

    FirstLaunchConsentScreen(
        uiState = uiState,
        onLegalCheckedChange = viewModel::onLegalCheckedChanged,
        onServiceNatureAckChange = viewModel::onServiceNatureAckChanged,
        onOpenLegal = { legalDoc = it },
        onAccept = viewModel::accept,
        onDecline = onDecline,
        modifier = modifier,
        spec = spec,
    )
}

@Composable
fun FirstLaunchConsentScreen(
    uiState: FirstLaunchConsentUiState,
    onLegalCheckedChange: (Boolean) -> Unit,
    onServiceNatureAckChange: (Boolean) -> Unit,
    onOpenLegal: (LegalDocType) -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
    spec: com.delta.helper.screen.layout.HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
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
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = LegalCopy.FIRST_LAUNCH_TITLE,
                    style = MaterialTheme.typography.headlineSmall,
                    color = HzColors.TextPrimary,
                )
                Text(
                    text = LegalCopy.FIRST_LAUNCH_SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HzColors.TextSecondary,
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LegalCopy.FIRST_LAUNCH_SUMMARY_BULLETS.forEach { bullet ->
                        Text(
                            text = "· $bullet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HzColors.TextSecondary,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                        )
                    }
                }

                Text(
                    text = LegalCopy.FIRST_LAUNCH_PAYMENT_HINT,
                    modifier = Modifier.clickable { onOpenLegal(LegalDocType.Payment) },
                    style = MaterialTheme.typography.bodySmall,
                    color = HzColors.Primary,
                )

                Spacer(modifier = Modifier.height(4.dp))

                HzLegalAgreementRow(
                    checked = uiState.legalAccepted,
                    onCheckedChange = onLegalCheckedChange,
                    onOpenLegal = onOpenLegal,
                    prefix = LegalCopy.FIRST_LAUNCH_LEGAL_PREFIX,
                    showPayment = false,
                )
                HzPlainAckRow(
                    checked = uiState.serviceNatureAcknowledged,
                    onCheckedChange = onServiceNatureAckChange,
                    text = LegalCopy.SERVICE_NATURE_ACK,
                )

                HzPrimaryButton(
                    text = LegalCopy.FIRST_LAUNCH_AGREE,
                    onClick = onAccept,
                    enabled = uiState.canAccept,
                )
                HzSecondaryButton(
                    text = LegalCopy.FIRST_LAUNCH_DECLINE,
                    onClick = onDecline,
                )

                Text(
                    text = "更新日期：${LegalCopy.UPDATED_AT} · 协议版本 ${LegalCopy.AGREEMENT_VERSION}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = HzColors.TextMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

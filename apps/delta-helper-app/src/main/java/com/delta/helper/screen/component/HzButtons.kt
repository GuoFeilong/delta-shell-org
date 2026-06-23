package com.delta.helper.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun HzCardInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.BgCard)
            .border(1.dp, HzColors.InputCardBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = HzColors.TextPrimary),
            cursorBrush = SolidColor(HzColors.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HzColors.BgInput)
                .padding(horizontal = 16.dp),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = HzColors.TextMuted,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
fun HzPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val alpha = if (enabled) 1f else 0.45f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(HzColors.Primary, HzColors.PrimaryDark),
                ),
                alpha = alpha,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = HzColors.TextPrimary.copy(alpha = alpha),
        )
    }
}

@Composable
fun HzSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.BgElevated)
            .border(1.dp, HzColors.Border, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = HzColors.TextPrimary,
        )
    }
}

@Composable
fun HzLegalAgreementRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onOpenLegal: (com.delta.helper.screen.legal.LegalDocType) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String = "激活前请阅读并同意",
    showPayment: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (checked) HzColors.Primary else HzColors.BgInput)
                .border(1.dp, if (checked) HzColors.Primary else HzColors.Border, RoundedCornerShape(4.dp))
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = HzColors.TextPrimary,
                )
            }
        }
        Text(
            text = buildLegalAgreementText(
                prefix = prefix,
                showPayment = showPayment,
                onOpenLegal = onOpenLegal,
            ),
            style = MaterialTheme.typography.bodySmall.copy(
                color = HzColors.TextMuted,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            ),
        )
    }
}

@Composable
private fun buildLegalAgreementText(
    prefix: String,
    showPayment: Boolean,
    onOpenLegal: (com.delta.helper.screen.legal.LegalDocType) -> Unit,
): androidx.compose.ui.text.AnnotatedString {
    val linkStyle = MaterialTheme.typography.bodySmall.copy(
        color = HzColors.Primary,
    ).toSpanStyle()
    return androidx.compose.ui.text.buildAnnotatedString {
        append(prefix)
        pushLink(
            androidx.compose.ui.text.LinkAnnotation.Clickable(
                tag = "user",
                linkInteractionListener = {
                    onOpenLegal(com.delta.helper.screen.legal.LegalDocType.User)
                },
            ),
        )
        withStyle(linkStyle) { append("《用户服务协议》") }
        pop()
        append("、")
        pushLink(
            androidx.compose.ui.text.LinkAnnotation.Clickable(
                tag = "privacy",
                linkInteractionListener = {
                    onOpenLegal(com.delta.helper.screen.legal.LegalDocType.Privacy)
                },
            ),
        )
        withStyle(linkStyle) { append("《隐私政策》") }
        pop()
        if (showPayment) {
            append("及")
            pushLink(
                androidx.compose.ui.text.LinkAnnotation.Clickable(
                    tag = "payment",
                    linkInteractionListener = {
                        onOpenLegal(com.delta.helper.screen.legal.LegalDocType.Payment)
                    },
                ),
            )
            withStyle(linkStyle) { append("《付费与激活说明》") }
            pop()
        }
    }
}

@Composable
fun HzTopBar(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = HzColors.TextPrimary,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = HzColors.TextMuted,
                letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = HzColors.TextPrimary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun HzInlineMessage(
    message: String,
    modifier: Modifier = Modifier,
    isError: Boolean = true,
) {
    Text(
        text = message,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) HzColors.Error else HzColors.Primary,
        textAlign = TextAlign.Center,
    )
}

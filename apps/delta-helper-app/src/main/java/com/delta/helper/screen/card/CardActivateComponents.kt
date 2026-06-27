package com.delta.helper.screen.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun CardActivateHero(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CardKeyIconBadge()

        Text(
            text = CardActivateCopy.PAGE_SUBTITLE,
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 2,
                fontWeight = FontWeight.Medium,
                brush = Brush.linearGradient(
                    colors = listOf(HzColors.Primary, HzColors.PrimaryLight),
                ),
            ),
        )

        Text(
            text = CardActivateCopy.PAGE_TITLE,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.TextPrimary,
                        HzColors.PrimaryLight,
                        HzColors.Primary,
                    ),
                ),
            ),
            textAlign = TextAlign.Center,
        )

        Text(
            text = CardActivateCopy.PAGE_DESC,
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = HzColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .padding(top = 14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(HzColors.Primary.copy(alpha = 0.1f))
                .border(1.dp, HzColors.Primary.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = CardActivateCopy.HERO_BADGE,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = HzColors.Primary,
            )
        }
    }
}

@Composable
private fun CardKeyIconBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.28f),
                        HzColors.BgCard,
                    ),
                ),
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.85f),
                        HzColors.PrimaryDark.copy(alpha = 0.35f),
                        HzColors.Border,
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = HzColors.PrimaryLight,
            modifier = Modifier.size(34.dp),
        )
    }
}

@Composable
fun CardActivateGlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard.copy(alpha = 0.92f),
                        HzColors.BgElevated.copy(alpha = 0.88f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.55f),
                        HzColors.Primary.copy(alpha = 0.08f),
                        HzColors.Border,
                    ),
                ),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(18.dp),
    ) {
        content()
    }
}

@Composable
fun CardActivateNoticeStrip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.NoticeBackground)
            .border(1.dp, HzColors.NoticeBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "ⓘ",
            style = MaterialTheme.typography.labelMedium,
            color = HzColors.Warning,
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextSecondary,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
        )
    }
}

@Composable
fun CardActivateSegmentTabs(
    selectedMode: CardActivateAccessMode,
    onModeSelected: (CardActivateAccessMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.BgElevated)
            .border(1.dp, HzColors.Border, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CardActivateSegmentTab(
            text = CardActivateCopy.TAB_PURCHASE,
            selected = selectedMode == CardActivateAccessMode.PURCHASE,
            onClick = { onModeSelected(CardActivateAccessMode.PURCHASE) },
            modifier = Modifier.weight(1f),
        )
        CardActivateSegmentTab(
            text = CardActivateCopy.TAB_ACTIVATE,
            selected = selectedMode == CardActivateAccessMode.ACTIVATE,
            onClick = { onModeSelected(CardActivateAccessMode.ACTIVATE) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CardActivateSegmentTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) {
                    Brush.linearGradient(listOf(HzColors.Primary.copy(alpha = 0.22f), HzColors.BgCard))
                } else {
                    Brush.linearGradient(listOf(HzColors.BgElevated, HzColors.BgElevated))
                },
            )
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) HzColors.Primary.copy(alpha = 0.45f) else HzColors.Border,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = if (selected) HzColors.PrimaryLight else HzColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CardActivatePurchaseLegalNote(
    onOpenPaymentDoc: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = CardActivateCopy.PURCHASE_LEGAL_HINT,
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextMuted,
            lineHeight = MaterialTheme.typography.labelSmall.lineHeight,
        )
        Text(
            text = buildAnnotatedString {
                append(CardActivateCopy.PURCHASE_PAYMENT_DOC_PREFIX)
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "payment",
                        linkInteractionListener = { onOpenPaymentDoc() },
                    ),
                ) {
                    withStyle(
                        MaterialTheme.typography.labelSmall.copy(color = HzColors.Primary).toSpanStyle(),
                    ) {
                        append(CardActivateCopy.PURCHASE_PAYMENT_DOC_LINK)
                    }
                }
            },
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextMuted,
        )
    }
}

@Composable
fun CardActivateTipsPanel(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HzColors.BgCard.copy(alpha = 0.72f))
            .border(1.dp, HzColors.Border, RoundedCornerShape(14.dp))
            .clickable { onExpandedChange(!expanded) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = CardActivateCopy.TIPS_TITLE,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = HzColors.TextSecondary,
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "收起" else "展开",
                tint = HzColors.Primary,
                modifier = Modifier.size(20.dp),
            )
        }
        if (expanded) {
            Text(
                text = CardActivateCopy.TIPS_TEXT,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextMuted,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            )
        }
    }
}

@Composable
fun CardActivateTipsCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HzColors.BgCard.copy(alpha = 0.72f))
            .border(1.dp, HzColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = CardActivateCopy.TIPS_TITLE,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = HzColors.TextSecondary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextMuted,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
        )
    }
}

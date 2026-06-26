package com.delta.helper.screen.component

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.delta.helper.activation.ActivationPlanVisualThemes
import com.delta.helper.activation.HelperActivationUiState
import com.delta.helper.ui.theme.HzColors

@Composable
fun HzActivationAccessBanner(
    state: HelperActivationUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.gateEnabled) return

    val activated = state.activated
    val theme = ActivationPlanVisualThemes.resolve(state.planCode, activated)
    val shape = RoundedCornerShape(16.dp)
    val background = if (activated) {
        Brush.linearGradient(
            colors = listOf(
                HzColors.BgCard.copy(alpha = 0.95f),
                theme.backgroundTint,
                theme.secondaryTint,
            ),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                HzColors.BgCard.copy(alpha = 0.95f),
                HzColors.Warning.copy(alpha = 0.08f),
            ),
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(1.dp, theme.borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.bannerTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = theme.accent,
                )
                if (theme.showPlanBadge && !theme.planLabel.isNullOrBlank()) {
                    HzAccessPlanBadge(label = theme.planLabel, accent = theme.accent)
                }
            }
            ActivationBannerSubtitle(
                sourceLine = state.bannerSourceLine,
                accessLine = state.accessLine,
                fallback = state.bannerSubtitle,
                accent = theme.accent,
                activated = activated,
            )
        }
        Text(
            text = if (activated) "查看 ›" else "去开通 ›",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = theme.accent,
        )
    }
}

@Composable
private fun ActivationBannerSubtitle(
    sourceLine: String,
    accessLine: String,
    fallback: String,
    accent: Color,
    activated: Boolean,
) {
    val source = sourceLine.trim()
    val access = accessLine.trim()
    if (activated && (source.isNotEmpty() || access.isNotEmpty())) {
        Text(
            text = buildAnnotatedString {
                if (source.isNotEmpty()) {
                    withStyle(SpanStyle(color = HzColors.TextSecondary)) {
                        append(source)
                    }
                }
                if (source.isNotEmpty() && access.isNotEmpty()) {
                    withStyle(SpanStyle(color = HzColors.TextMuted)) {
                        append(" · ")
                    }
                }
                if (access.isNotEmpty()) {
                    withStyle(
                        SpanStyle(
                            color = accent,
                            fontWeight = FontWeight.Medium,
                        ),
                    ) {
                        append(access)
                    }
                }
            },
            style = MaterialTheme.typography.bodySmall,
        )
        return
    }
    Text(
        text = fallback,
        style = MaterialTheme.typography.bodySmall,
        color = if (activated) accent.copy(alpha = 0.85f) else HzColors.TextSecondary,
    )
}

@Composable
private fun HzAccessPlanBadge(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = accent,
    )
}

@Composable
fun HzPlanRadioDot(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) HzColors.Primary else HzColors.Border,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(HzColors.Primary),
            )
        }
    }
}

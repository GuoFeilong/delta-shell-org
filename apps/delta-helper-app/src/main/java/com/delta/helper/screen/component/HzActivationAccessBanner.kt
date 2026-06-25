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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (activated) {
        HzColors.Primary.copy(alpha = 0.35f)
    } else {
        HzColors.Warning.copy(alpha = 0.35f)
    }
    val background = if (activated) {
        Brush.linearGradient(
            colors = listOf(
                HzColors.BgCard.copy(alpha = 0.95f),
                HzColors.Primary.copy(alpha = 0.08f),
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
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = state.bannerTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (activated) HzColors.Primary else HzColors.Warning,
            )
            Text(
                text = state.bannerSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextSecondary,
            )
        }
        Text(
            text = if (activated) "查看 ›" else "去开通 ›",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = HzColors.Primary,
        )
    }
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

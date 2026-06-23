package com.delta.helper.screen.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun GameProfileCard(
    item: GameProfileItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "card-scale",
    )

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard,
                        HzColors.BgElevated.copy(alpha = 0.92f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        item.accent.copy(alpha = if (pressed) 0.85f else 0.55f),
                        item.accent.copy(alpha = 0.08f),
                        HzColors.Border,
                    ),
                ),
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            GameIconBadge(
                iconRes = item.iconRes,
                contentDescription = item.title,
                accent = item.accent,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = HzColors.TextPrimary,
                    ),
                )
                Text(
                    text = item.tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = HzColors.TextMuted,
                )
                GameFpsBadges(accent = item.accent)
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.headlineSmall,
                color = item.accent.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun GameFpsBadges(
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = HelperHomeCopy.CARD_QUALITY_LABEL,
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextSecondary,
        )
        FpsBadge(label = HelperHomeCopy.FPS_144_LABEL, accent = accent)
        Text(
            text = "/",
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextMuted,
        )
        FpsBadge(label = HelperHomeCopy.FPS_165_LABEL, accent = accent, emphasized = true)
    }
}

@Composable
private fun FpsBadge(
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (emphasized) accent.copy(alpha = 0.18f) else HzColors.BgInput,
            )
            .border(
                width = 1.dp,
                color = if (emphasized) accent.copy(alpha = 0.55f) else HzColors.Border,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = if (emphasized) accent else HzColors.TextSecondary,
        )
    }
}

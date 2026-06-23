package com.delta.helper.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.delta.helper.screen.game.FpsOption
import com.delta.helper.screen.game.FpsSettingCopy
import com.delta.helper.ui.theme.HzColors

@Composable
fun HzFpsSettingSection(
    options: List<FpsOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard,
                        HzColors.BgElevated.copy(alpha = 0.88f),
                    ),
                ),
            )
            .border(1.dp, HzColors.Border, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = FpsSettingCopy.SECTION_TITLE,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = accent,
            ),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = FpsSettingCopy.FPS_TITLE,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = HzColors.TextPrimary,
                ),
            )
            Text(
                text = FpsSettingCopy.FPS_HINT,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextMuted,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            )
        }

        HzFpsSegmentBar(
            options = options,
            selectedId = selectedId,
            onSelect = onSelect,
            accent = accent,
        )

        Text(
            text = FpsSettingCopy.PREVIEW_NOTE,
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextMuted,
            lineHeight = MaterialTheme.typography.labelSmall.lineHeight,
        )
    }
}

@Composable
fun HzFpsSegmentBar(
    options: List<FpsOption>,
    selectedId: String,
    onSelect: (String) -> Unit,
    accent: Color,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = options.size <= 4,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.BgInput.copy(alpha = 0.95f))
            .border(1.dp, HzColors.Border.copy(alpha = 0.85f), RoundedCornerShape(12.dp)),
    ) {
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(HzColors.Border.copy(alpha = 0.75f)),
                )
            }

            FpsSegmentCell(
                option = option,
                selected = option.id == selectedId,
                accent = accent,
                onClick = { onSelect(option.id) },
                modifier = if (equalWidth) Modifier.weight(1f) else Modifier.widthIn(min = 52.dp),
            )
        }
    }
}

@Composable
private fun FpsSegmentCell(
    option: FpsOption,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected -> accent
            option.isHighFps -> accent.copy(alpha = 0.08f)
            else -> Color.Transparent
        },
        animationSpec = tween(180),
        label = "fps-segment-bg",
    )
    val textColor by animateColorAsState(
        targetValue = when {
            selected -> HzColors.BgPrimary
            option.isHighFps -> accent.copy(alpha = 0.82f)
            else -> HzColors.TextPrimary.copy(alpha = 0.88f)
        },
        animationSpec = tween(180),
        label = "fps-segment-text",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = option.label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            ),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

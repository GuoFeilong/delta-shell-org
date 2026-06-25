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
import com.delta.helper.activation.ActivationPlanDisplayFormatter
import com.delta.helper.screen.card.CardPurchaseOptionUi
import com.delta.helper.screen.legal.LegalCopy
import com.delta.helper.ui.theme.HzColors

@Composable
fun HzPlanPicker(
    options: List<CardPurchaseOptionUi>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "选择规格",
    hint: String = LegalCopy.PLAN_PICKER_HINT,
) {
    if (options.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard,
                        HzColors.Primary.copy(alpha = 0.05f),
                    ),
                ),
            )
            .border(1.dp, HzColors.Primary.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = HzColors.TextPrimary,
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextMuted,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEachIndexed { index, option ->
                HzPlanPickerCard(
                    option = option,
                    selected = index == selectedIndex,
                    onClick = { onSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun HzPlanPickerCard(
    option: CardPurchaseOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val display = ActivationPlanDisplayFormatter.format(option)
    val shape = RoundedCornerShape(14.dp)
    val borderColor = when {
        selected && display.isLifetime -> HzColors.Warning.copy(alpha = 0.5f)
        selected -> HzColors.Primary.copy(alpha = 0.55f)
        display.isLifetime -> HzColors.Warning.copy(alpha = 0.28f)
        else -> HzColors.Border
    }
    val background = when {
        selected && display.isLifetime -> Brush.linearGradient(
            colors = listOf(
                HzColors.Primary.copy(alpha = 0.08f),
                HzColors.Warning.copy(alpha = 0.1f),
            ),
        )
        selected -> Brush.linearGradient(
            colors = listOf(HzColors.Primary.copy(alpha = 0.1f), HzColors.BgElevated),
        )
        display.isLifetime -> Brush.linearGradient(
            colors = listOf(HzColors.Warning.copy(alpha = 0.04f), HzColors.BgElevated),
        )
        else -> Brush.linearGradient(
            colors = listOf(HzColors.BgElevated, HzColors.BgElevated),
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HzPlanRadioDot(selected = selected)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = display.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) HzColors.PrimaryLight else HzColors.TextPrimary,
                )
                when {
                    display.isDefault -> HzPlanBadge(text = "推荐")
                    display.isLifetime -> HzPlanBadge(text = "尊享", gold = true)
                }
            }
            if (display.subtitle.isNotBlank()) {
                Text(
                    text = display.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HzColors.TextSecondary,
                )
            }
        }

        display.priceDisplay?.let { price ->
            Text(
                text = price,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = HzColors.Warning,
            )
        }
    }
}

@Composable
private fun HzPlanBadge(
    text: String,
    gold: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val color = if (gold) HzColors.Warning else HzColors.Primary
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = color,
    )
}

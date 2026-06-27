package com.delta.helper.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.helper.activation.ActivationPlanDisplay
import com.delta.helper.activation.ActivationPlanDisplayFormatter
import com.delta.helper.screen.card.CardActivateCopy
import com.delta.helper.screen.card.CardPurchaseOptionUi
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.delay

@Composable
fun HzPlanPicker(
    options: List<CardPurchaseOptionUi>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = CardActivateCopy.PLAN_PICKER_TITLE,
    hint: String = CardActivateCopy.PLAN_PICKER_HINT,
    embedded: Boolean = false,
) {
    if (options.isEmpty()) return

    var cardsVisible by remember(options) { mutableStateOf(false) }
    val yearHighlightIndex = remember(options) {
        options.indexOfFirst {
            it.planCode.trim().equals("YEAR", ignoreCase = true) && it.default
        }.takeIf { it >= 0 }
            ?: options.indexOfFirst { it.planCode.trim().equals("YEAR", ignoreCase = true) }
    }
    LaunchedEffect(options) {
        cardsVisible = false
        delay(80)
        cardsVisible = true
    }

    val content: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    AnimatedVisibility(
                        visible = cardsVisible,
                        enter = fadeIn(tween(320, delayMillis = index * 70, easing = FastOutSlowInEasing)) +
                            slideInVertically(
                                tween(320, delayMillis = index * 70, easing = FastOutSlowInEasing),
                            ) { it / 10 },
                    ) {
                        HzPlanPickerCard(
                            option = option,
                            selected = index == selectedIndex,
                            onClick = { onSelected(index) },
                            cardIndex = index,
                            playEntryBounce = cardsVisible &&
                                index == yearHighlightIndex &&
                                yearHighlightIndex >= 0,
                        )
                    }
                }
            }

            Text(
                text = "价格仅供参考，以购买页实际结算为准",
                style = MaterialTheme.typography.labelSmall,
                color = HzColors.TextMuted,
            )
        }
    }

    if (embedded) {
        Column(modifier = modifier.fillMaxWidth()) {
            content()
        }
        return
    }

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
    ) {
        content()
    }
}

@Composable
private fun HzPlanPickerCard(
    option: CardPurchaseOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
    cardIndex: Int,
    playEntryBounce: Boolean,
    modifier: Modifier = Modifier,
) {
    val display = ActivationPlanDisplayFormatter.format(option)
    val shape = RoundedCornerShape(14.dp)
    val entryScale = remember(option.planCode, option.default) { Animatable(1f) }
    var entryBouncePlayed by remember(option.planCode, option.default) { mutableStateOf(false) }

    LaunchedEffect(playEntryBounce) {
        if (!playEntryBounce || entryBouncePlayed || !display.isYear) return@LaunchedEffect
        entryBouncePlayed = true
        delay(80L + cardIndex * 70L + 320L)
        entryScale.snapTo(1f)
        entryScale.animateTo(1.034f, tween(200, easing = FastOutSlowInEasing))
        entryScale.animateTo(0.992f, tween(140, easing = FastOutSlowInEasing))
        entryScale.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
    }

    val selectionScale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.985f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "planCardScale",
    )
    val combinedScale = selectionScale * entryScale.value
    val borderColor = when {
        selected && display.isLifetime -> HzColors.PlanPromo.copy(alpha = 0.62f)
        selected && display.isDefault -> HzColors.Primary.copy(alpha = 0.62f)
        selected -> HzColors.Primary.copy(alpha = 0.55f)
        display.isDefault -> HzColors.Primary.copy(alpha = 0.38f)
        display.isLifetime && display.hasPromo -> HzColors.PlanPromo.copy(alpha = 0.32f)
        display.isLifetime -> HzColors.Warning.copy(alpha = 0.28f)
        else -> HzColors.Border
    }
    val borderWidth = when {
        selected && (display.isLifetime || display.isDefault) -> 1.5.dp
        display.isDefault && !selected -> 1.25.dp
        else -> 1.dp
    }
    val radioAccent = when {
        selected && display.isLifetime -> HzColors.PlanPromo
        selected -> HzColors.Primary
        else -> HzColors.Primary
    }
    val background = when {
        selected && display.isLifetime -> Brush.linearGradient(
            colors = listOf(
                HzColors.PlanPromo.copy(alpha = 0.1f),
                HzColors.Warning.copy(alpha = 0.08f),
            ),
        )
        selected && display.isDefault -> Brush.linearGradient(
            colors = listOf(HzColors.Primary.copy(alpha = 0.12f), HzColors.BgElevated),
        )
        selected -> Brush.linearGradient(
            colors = listOf(HzColors.Primary.copy(alpha = 0.1f), HzColors.BgElevated),
        )
        display.isLifetime && display.hasPromo -> Brush.linearGradient(
            colors = listOf(HzColors.PlanPromo.copy(alpha = 0.06f), HzColors.BgElevated),
        )
        display.isDefault -> Brush.linearGradient(
            colors = listOf(HzColors.Primary.copy(alpha = 0.05f), HzColors.BgElevated),
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
            .scale(combinedScale)
            .clip(shape)
            .background(background)
            .border(borderWidth, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HzPlanRadioDot(selected = selected, accentColor = radioAccent)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = display.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (display.isLifetime || display.isDefault) FontWeight.Bold else FontWeight.SemiBold,
                    ),
                    color = planTitleColor(display, selected),
                )
                if (display.isDefault) {
                    HzPlanBadge(text = "推荐")
                }
                if (display.isLifetime) {
                    HzPlanBadge(text = "尊享", gold = true)
                    display.promoBadge?.let { HzPlanBadge(text = it, promo = true) }
                }
            }
            if (display.subtitle.isNotBlank()) {
                Text(
                    text = display.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (display.isMonth && !selected) {
                        HzColors.TextMuted
                    } else {
                        HzColors.TextSecondary
                    },
                )
            }
            display.valueProposition?.let { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = when {
                        display.isLifetime -> HzColors.PlanPromo.copy(alpha = 0.92f)
                        display.isDefault -> HzColors.PrimaryLight.copy(alpha = 0.88f)
                        else -> HzColors.TextMuted
                    },
                )
            }
        }

        if (display.priceDisplay != null) {
            HzPlanPriceColumn(display = display, selected = selected)
        }
    }
}

@Composable
private fun planTitleColor(display: ActivationPlanDisplay, selected: Boolean): Color = when {
    selected && display.isLifetime -> HzColors.PlanPromo
    selected -> HzColors.PrimaryLight
    display.isLifetime -> HzColors.PlanLifetime
    display.isDefault -> HzColors.PrimaryLight.copy(alpha = 0.9f)
    display.isMonth -> HzColors.TextSecondary
    else -> HzColors.TextPrimary
}

@Composable
private fun HzPlanPriceColumn(
    display: ActivationPlanDisplay,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val currentColor = when {
        display.isLifetime -> HzColors.PlanPromo
        display.isYear && (selected || display.isDefault) -> HzColors.PrimaryLight
        display.isYear -> HzColors.PlanYear
        display.isMonth -> HzColors.PlanMonth.copy(alpha = if (selected) 0.95f else 0.72f)
        selected -> HzColors.PrimaryLight
        else -> HzColors.Warning.copy(alpha = 0.88f)
    }
    val priceStyle = when {
        display.isLifetime -> MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
        display.isYear && display.hasPromo -> MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        display.isMonth -> MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
        )
        else -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(if (display.isLifetime && display.hasPromo) 4.dp else 2.dp),
    ) {
        Text(
            text = display.priceDisplay.orEmpty(),
            style = priceStyle,
            color = currentColor,
        )
        display.originalPriceDisplay?.let { original ->
            Text(
                text = original,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextMuted,
                textDecoration = TextDecoration.LineThrough,
            )
        }
        display.savingsDisplay?.let { savings ->
            HzPlanSavingsBadge(
                text = savings,
                prominent = display.isLifetime || display.isYear,
                pulse = display.isLifetime && display.hasPromo,
            )
        }
    }
}

@Composable
private fun HzPlanSavingsBadge(
    text: String,
    prominent: Boolean,
    pulse: Boolean,
    modifier: Modifier = Modifier,
) {
    val pulseAlpha = if (pulse) {
        val transition = rememberInfiniteTransition(label = "savingsPulse")
        transition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.32f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "savingsPulseAlpha",
        ).value
    } else {
        if (prominent) 0.22f else 0.14f
    }

    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(HzColors.PlanPromo.copy(alpha = pulseAlpha))
            .border(
                1.dp,
                HzColors.PlanPromo.copy(alpha = if (prominent) 0.55f else 0.4f),
                RoundedCornerShape(999.dp),
            )
            .padding(
                horizontal = if (prominent) 10.dp else 8.dp,
                vertical = if (prominent) 3.dp else 2.dp,
            ),
        style = if (prominent) {
            MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        } else {
            MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        },
        color = HzColors.PlanPromo,
    )
}

@Composable
private fun HzPlanBadge(
    text: String,
    gold: Boolean = false,
    promo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val color = when {
        promo -> HzColors.PlanPromo
        gold -> HzColors.Warning
        else -> HzColors.Primary
    }
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = if (promo) 0.16f else 0.12f))
            .border(1.dp, color.copy(alpha = if (promo) 0.45f else 0.35f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = color,
    )
}

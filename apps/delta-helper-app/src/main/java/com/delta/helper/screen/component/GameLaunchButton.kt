package com.delta.helper.screen.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
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
import androidx.compose.ui.unit.sp
import com.delta.helper.screen.game.GameLaunchCopy
import com.delta.helper.ui.theme.HzColors

/** 四角等宽 45° 切角，水平/垂直对称 */
private fun tacticalButtonShape(chamferRatio: Float = 0.22f) = GenericShape { size, _ ->
    val chamfer = minOf(size.width, size.height) * chamferRatio
    moveTo(chamfer, 0f)
    lineTo(size.width - chamfer, 0f)
    lineTo(size.width, chamfer)
    lineTo(size.width, size.height - chamfer)
    lineTo(size.width - chamfer, size.height)
    lineTo(chamfer, size.height)
    lineTo(0f, size.height - chamfer)
    lineTo(0f, chamfer)
    close()
}

@Composable
fun GameLaunchDock(
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.45f to HzColors.BgPrimary.copy(alpha = 0.85f),
                        1f to HzColors.BgPrimary,
                    ),
                ),
            )
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        GameLaunchButton(
            accent = accent,
            onClick = onClick,
            enabled = enabled,
        )
    }
}

@Composable
fun GameLaunchButton(
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = tween(100),
        label = "launch-scale",
    )
    val alpha = if (enabled) 1f else 0.4f
    val outerShape = remember { tacticalButtonShape(chamferRatio = 0.22f) }
    val innerShape = remember { tacticalButtonShape(chamferRatio = 0.22f) }

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(outerShape)
                .background(HzColors.BgInput)
                .border(1.5.dp, accent.copy(alpha = 0.65f * alpha), outerShape),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(4.dp)
                .clip(innerShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            accent.copy(alpha = alpha),
                            accentDarken(accent).copy(alpha = alpha),
                        ),
                    ),
                )
                .border(1.dp, Color.White.copy(alpha = 0.18f * alpha), innerShape)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = GameLaunchCopy.BUTTON_LABEL,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 10.sp,
                    color = HzColors.BgPrimary.copy(alpha = alpha),
                ),
            )
        }
    }
}

private fun accentDarken(accent: Color): Color = Color(
    red = accent.red * 0.72f,
    green = accent.green * 0.72f,
    blue = accent.blue * 0.72f,
    alpha = accent.alpha,
)

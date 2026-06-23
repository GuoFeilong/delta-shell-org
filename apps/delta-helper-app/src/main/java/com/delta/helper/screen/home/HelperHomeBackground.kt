package com.delta.helper.screen.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun HelperHomeBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "home-bg")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(HzColors.BgPrimary)

        val w = size.width
        val h = size.height

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HzColors.Primary.copy(alpha = 0.22f * pulse),
                    HzColors.Primary.copy(alpha = 0f),
                ),
                center = Offset(w * (0.15f + drift * 0.08f), h * 0.12f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * (0.15f + drift * 0.08f), h * 0.12f),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HzColors.PrimaryDark.copy(alpha = 0.14f),
                    HzColors.PrimaryDark.copy(alpha = 0f),
                ),
                center = Offset(w * (0.85f - drift * 0.06f), h * 0.78f),
                radius = w * 0.45f,
            ),
            radius = w * 0.45f,
            center = Offset(w * (0.85f - drift * 0.06f), h * 0.78f),
        )

        val gridStep = 32.dp.toPx()
        val gridColor = HzColors.Border.copy(alpha = 0.35f)
        var x = 0f
        while (x <= w) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
            x += gridStep
        }
        var y = 0f
        while (y <= h) {
            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            y += gridStep
        }

        val scanY = h * (0.2f + drift * 0.6f)
        drawLine(
            color = HzColors.Primary.copy(alpha = 0.08f),
            start = Offset(0f, scanY),
            end = Offset(w, scanY),
            strokeWidth = 2.dp.toPx(),
        )

        val corner = 48.dp.toPx()
        val framePath = Path().apply {
            moveTo(16.dp.toPx(), corner)
            lineTo(16.dp.toPx(), 16.dp.toPx())
            lineTo(corner, 16.dp.toPx())
            moveTo(w - corner, 16.dp.toPx())
            lineTo(w - 16.dp.toPx(), 16.dp.toPx())
            lineTo(w - 16.dp.toPx(), corner)
            moveTo(w - 16.dp.toPx(), h - corner)
            lineTo(w - 16.dp.toPx(), h - 16.dp.toPx())
            lineTo(w - corner, h - 16.dp.toPx())
            moveTo(corner, h - 16.dp.toPx())
            lineTo(16.dp.toPx(), h - 16.dp.toPx())
            lineTo(16.dp.toPx(), h - corner)
        }
        drawPath(
            path = framePath,
            color = HzColors.Primary.copy(alpha = 0.25f),
            style = Stroke(width = 1.5.dp.toPx()),
        )
    }
}

package com.delta.helper.screen.splash

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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashAnimatedBackground(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "splash-bg")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sweep",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(HzColors.BgPrimary)

        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.46f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HzColors.Primary.copy(alpha = 0.28f * pulse),
                    HzColors.Primary.copy(alpha = 0f),
                ),
                center = Offset(w * (0.12f + drift * 0.1f), h * 0.08f),
                radius = w * 0.72f,
            ),
            radius = w * 0.72f,
            center = Offset(w * (0.12f + drift * 0.1f), h * 0.08f),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HzColors.PrimaryDark.copy(alpha = 0.2f),
                    HzColors.PrimaryDark.copy(alpha = 0f),
                ),
                center = Offset(w * (0.88f - drift * 0.08f), h * 0.92f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * (0.88f - drift * 0.08f), h * 0.92f),
        )

        rotate(sweep, pivot = Offset(cx, cy)) {
            for (index in 0 until 3) {
                val angle = index * 120f
                val bandWidth = w * 0.22f
                val length = w * 1.4f
                val rad = Math.toRadians(angle.toDouble())
                val dx = cos(rad).toFloat()
                val dy = sin(rad).toFloat()
                val start = Offset(cx - dx * length, cy - dy * length)
                val end = Offset(cx + dx * length, cy + dy * length)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            HzColors.Primary.copy(alpha = 0f),
                            HzColors.Primary.copy(alpha = 0.14f * pulse),
                            HzColors.PrimaryLight.copy(alpha = 0.22f * pulse),
                            HzColors.Primary.copy(alpha = 0.14f * pulse),
                            HzColors.Primary.copy(alpha = 0f),
                        ),
                        start = start,
                        end = end,
                    ),
                    start = start,
                    end = end,
                    strokeWidth = bandWidth,
                )
            }
        }

        val scanY = h * (0.15f + drift * 0.7f)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    HzColors.Primary.copy(alpha = 0f),
                    HzColors.Primary.copy(alpha = 0.16f * pulse),
                    HzColors.PrimaryLight.copy(alpha = 0.24f * pulse),
                    HzColors.Primary.copy(alpha = 0.16f * pulse),
                    HzColors.Primary.copy(alpha = 0f),
                ),
                startX = 0f,
                endX = w,
            ),
            start = Offset(0f, scanY),
            end = Offset(w, scanY),
            strokeWidth = 3.dp.toPx(),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HzColors.Primary.copy(alpha = 0f),
                    HzColors.BgPrimary.copy(alpha = 0.55f),
                    HzColors.BgPrimary,
                ),
                center = Offset(cx, cy),
                radius = w * 0.75f,
            ),
            radius = w * 0.75f,
            center = Offset(cx, cy),
        )

        val corner = 52.dp.toPx()
        val inset = 20.dp.toPx()
        val framePath = Path().apply {
            moveTo(inset, corner)
            lineTo(inset, inset)
            lineTo(corner, inset)
            moveTo(w - corner, inset)
            lineTo(w - inset, inset)
            lineTo(w - inset, corner)
            moveTo(w - inset, h - corner)
            lineTo(w - inset, h - inset)
            lineTo(w - corner, h - inset)
            moveTo(corner, h - inset)
            lineTo(inset, h - inset)
            lineTo(inset, h - corner)
        }
        drawPath(
            path = framePath,
            color = HzColors.Primary.copy(alpha = 0.35f),
            style = Stroke(width = 1.5.dp.toPx()),
        )
    }
}

package com.delta.helper.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.R
import com.delta.helper.screen.home.GameIconBadge
import com.delta.helper.screen.home.HelperHomeCopy
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val DeltaAccent = Color(0xFF00D4AA)

@Composable
fun CustomSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconScale = remember { Animatable(0.55f) }
    val iconAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(28f) }

    val ringTransition = rememberInfiniteTransition(label = "splash-ring")
    val ringRotation by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring-rotation",
    )
    val ringPulse by ringTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ring-pulse",
    )

    LaunchedEffect(onFinished) {
        launch {
            iconAlpha.animateTo(1f, tween(420))
        }
        launch {
            iconScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.62f, stiffness = 380f),
            )
        }
        delay(380.milliseconds)
        launch {
            textAlpha.animateTo(1f, tween(520))
        }
        launch {
            textOffset.animateTo(0f, tween(520, easing = LinearEasing))
        }
        delay(1_650.milliseconds)
        onFinished()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        SplashAnimatedBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(168.dp)
                    .scale(iconScale.value)
                    .alpha(iconAlpha.value),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val stroke = 2.5.dp.toPx()
                    val glowRadius = size.minDimension * 0.46f * ringPulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DeltaAccent.copy(alpha = 0.35f),
                                DeltaAccent.copy(alpha = 0f),
                            ),
                            center = center,
                            radius = glowRadius,
                        ),
                        radius = glowRadius,
                        center = center,
                    )
                    rotate(ringRotation, pivot = center) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    DeltaAccent.copy(alpha = 0f),
                                    DeltaAccent.copy(alpha = 0.85f),
                                    HzColors.PrimaryLight.copy(alpha = 0.55f),
                                    DeltaAccent.copy(alpha = 0f),
                                ),
                                center = center,
                            ),
                            startAngle = 0f,
                            sweepAngle = 300f,
                            useCenter = false,
                            topLeft = Offset(
                                center.x - glowRadius,
                                center.y - glowRadius,
                            ),
                            size = Size(glowRadius * 2, glowRadius * 2),
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    }
                }

                GameIconBadge(
                    iconRes = R.drawable.ic_game_delta,
                    contentDescription = HelperHomeCopy.PAGE_TITLE,
                    accent = DeltaAccent,
                    size = 108.dp,
                    iconSize = 96.dp,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .padding(top = textOffset.value.dp),
            ) {
                Text(
                    text = HelperHomeCopy.BRAND_LABEL,
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 2.5f,
                        fontWeight = FontWeight.SemiBold,
                        brush = Brush.linearGradient(
                            colors = listOf(HzColors.Primary, HzColors.PrimaryLight),
                        ),
                    ),
                )

                Text(
                    text = HelperHomeCopy.PAGE_TITLE,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.displaySmall.copy(
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
                    text = HelperHomeCopy.PAGE_SUBTITLE,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = HzColors.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

package com.delta.helper.screen.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.helper.screen.game.GameLauncher
import com.delta.helper.screen.game.fpsLaunchNoticeMessage
import com.delta.helper.screen.game.fpsNumericDisplay
import com.delta.helper.screen.home.GameId
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.delay

private const val SUCCESS_AUTO_LAUNCH_MS = 2_000L
private const val ERROR_DISMISS_MS = 2_500L

data class GameLaunchNoticeSession(
    val fpsId: String,
)

private sealed interface GameLaunchNoticeStage {
    data object Hidden : GameLaunchNoticeStage

    data class Success(val fpsId: String) : GameLaunchNoticeStage

    data class Error(val message: String) : GameLaunchNoticeStage
}

@Composable
fun GameLaunchNoticeHost(
    session: GameLaunchNoticeSession?,
    gameId: GameId,
    accent: Color,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var stage by remember { mutableStateOf<GameLaunchNoticeStage>(GameLaunchNoticeStage.Hidden) }
    var flowToken by remember { mutableIntStateOf(0) }

    fun finishNotice() {
        stage = GameLaunchNoticeStage.Hidden
        onFinished()
    }

    fun tryLaunchGame() {
        val result = GameLauncher.launch(context, gameId)
        if (result.launched) {
            finishNotice()
        } else {
            stage = GameLaunchNoticeStage.Error(
                message = result.notInstalledMessage ?: GameLauncher.notInstalledMessage(gameId),
            )
        }
    }

    LaunchedEffect(session?.fpsId) {
        if (session?.fpsId == null) {
            stage = GameLaunchNoticeStage.Hidden
            return@LaunchedEffect
        }
        val fpsId = session.fpsId
        flowToken++
        val token = flowToken
        stage = GameLaunchNoticeStage.Success(fpsId)
        delay(SUCCESS_AUTO_LAUNCH_MS)
        if (flowToken != token || stage !is GameLaunchNoticeStage.Success) return@LaunchedEffect
        tryLaunchGame()
    }

    LaunchedEffect(stage) {
        if (stage !is GameLaunchNoticeStage.Error) return@LaunchedEffect
        delay(ERROR_DISMISS_MS)
        if (stage is GameLaunchNoticeStage.Error) {
            finishNotice()
        }
    }

    fun onNoticeClick() {
        when (val current = stage) {
            is GameLaunchNoticeStage.Success -> {
                flowToken++
                tryLaunchGame()
            }
            is GameLaunchNoticeStage.Error -> finishNotice()
            GameLaunchNoticeStage.Hidden -> Unit
        }
    }

    AnimatedVisibility(
        visible = stage !is GameLaunchNoticeStage.Hidden,
        modifier = modifier,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
    ) {
        AnimatedContent(
            targetState = stage,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "game-launch-notice",
        ) { current ->
            when (current) {
                is GameLaunchNoticeStage.Success -> {
                    GameLaunchNoticeCard(
                        accent = accent,
                        icon = {
                            NoticeIconBadge(
                                accent = accent,
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(22.dp),
                                    )
                                },
                            )
                        },
                        title = {
                            FpsActivatedTitle(fpsId = current.fpsId, accent = accent)
                        },
                        message = fpsLaunchNoticeMessage(current.fpsId),
                        onClick = ::onNoticeClick,
                    )
                }
                is GameLaunchNoticeStage.Error -> {
                    GameLaunchNoticeCard(
                        accent = HzColors.Error,
                        icon = {
                            NoticeIconBadge(
                                accent = HzColors.Error,
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = HzColors.Error,
                                        modifier = Modifier.size(22.dp),
                                    )
                                },
                            )
                        },
                        title = {
                            Text(
                                text = "游戏未安装",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = HzColors.Error,
                                ),
                            )
                        },
                        message = current.message,
                        onClick = ::onNoticeClick,
                    )
                }
                GameLaunchNoticeStage.Hidden -> Unit
            }
        }
    }
}

@Composable
private fun GameLaunchNoticeCard(
    accent: Color,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        HzColors.BgCard.copy(alpha = 0.96f),
                        HzColors.BgElevated.copy(alpha = 0.96f),
                    ),
                ),
            )
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                title()
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = HzColors.TextSecondary,
                        lineHeight = 18.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun NoticeIconBadge(
    accent: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.18f))
            .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun FpsActivatedTitle(
    fpsId: String,
    accent: Color,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = fpsNumericDisplay(fpsId),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = accent,
                letterSpacing = (-0.5).sp,
            ),
        )
        Text(
            text = "帧参考",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = accent.copy(alpha = 0.85f),
            ),
            modifier = Modifier.padding(bottom = 2.dp),
        )
    }
}

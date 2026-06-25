package com.delta.helper.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.activation.HelperActivationUiState
import com.delta.helper.screen.component.HzActivationAccessBanner
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.GameProfileCard
import com.delta.helper.screen.home.GameProfileItem
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.home.HelperHomeCopy
import com.delta.helper.screen.home.HelperHomeHero
import com.delta.helper.screen.home.helperHomeGames
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HelperHomeScreen(
    modifier: Modifier = Modifier,
    activationState: HelperActivationUiState = HelperActivationUiState(),
    onGameSelected: (GameId) -> Unit = {},
    onRequireActivation: () -> Unit = {},
) {
    val spec = rememberHelperAdaptiveSpec()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80.milliseconds)
        visible = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        HelperHomeBackground()

        HelperAdaptiveContainer(
            modifier = Modifier.fillMaxSize(),
            spec = spec,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = if (spec.isTabletOrFoldExpanded) 28.dp else 12.dp,
                        bottom = 28.dp,
                    ),
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 },
                ) {
                    HelperHomeHero()
                }

                if (activationState.gateEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(520, delayMillis = 60)),
                    ) {
                        HzActivationAccessBanner(
                            state = activationState,
                            onClick = onRequireActivation,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val columns = if (spec.isTabletOrFoldExpanded) 2 else 1
                if (columns == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        helperHomeGames.chunked(2).forEachIndexed { rowIndex, row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                row.forEachIndexed { colIndex, game ->
                                    val index = rowIndex * 2 + colIndex
                                    AnimatedGameCard(
                                        visible = visible,
                                        index = index,
                                        game = game,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onGameSelected(game.id) },
                                    )
                                }
                                if (row.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        helperHomeGames.forEachIndexed { index, game ->
                            AnimatedGameCard(
                                visible = visible,
                                index = index,
                                game = game,
                                onClick = { onGameSelected(game.id) },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 400)),
                ) {
                    HomeFootnote()
                }
            }
        }
    }
}

@Composable
private fun AnimatedGameCard(
    visible: Boolean,
    index: Int,
    game: GameProfileItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(450, delayMillis = 120 + index * 80)) +
            slideInVertically(tween(450, delayMillis = 120 + index * 80)) { it / 3 },
    ) {
        GameProfileCard(
            item = game,
            onClick = onClick,
        )
    }
}

@Composable
private fun HomeFootnote() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HzColors.BgCard.copy(alpha = 0.75f))
            .border(1.dp, HzColors.Border, RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Text(
            text = HelperHomeCopy.FOOTNOTE,
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextMuted,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

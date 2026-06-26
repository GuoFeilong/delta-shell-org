package com.delta.helper.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun GameHeroStrip(
    game: GameProfileItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard,
                        game.accent.copy(alpha = 0.08f),
                    ),
                ),
            )
            .border(1.dp, game.accent.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GameIconBadge(
            iconRes = game.iconRes,
            contentDescription = game.title,
            accent = game.accent,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = game.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = HzColors.TextPrimary,
                ),
            )
            Text(
                text = game.tagline,
                style = MaterialTheme.typography.bodySmall,
                color = HzColors.TextSecondary,
            )
            GameFpsHint(accent = game.accent)
        }
    }
}

@Composable
private fun GameFpsHint(
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "${HelperHomeCopy.CARD_QUALITY_LABEL} · ${HelperHomeCopy.FPS_144_LABEL} / ${HelperHomeCopy.FPS_165_LABEL}",
        modifier = modifier
            .padding(top = 2.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = accent,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

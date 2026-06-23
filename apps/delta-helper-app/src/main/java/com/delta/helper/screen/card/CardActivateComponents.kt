package com.delta.helper.screen.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun CardActivateHero(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CardKeyIconBadge()

        Text(
            text = CardActivateCopy.PAGE_SUBTITLE,
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 2,
                fontWeight = FontWeight.Medium,
                brush = Brush.linearGradient(
                    colors = listOf(HzColors.Primary, HzColors.PrimaryLight),
                ),
            ),
        )

        Text(
            text = CardActivateCopy.PAGE_TITLE,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.headlineMedium.copy(
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
            text = CardActivateCopy.PAGE_DESC,
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = HzColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .padding(top = 14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(HzColors.Primary.copy(alpha = 0.1f))
                .border(1.dp, HzColors.Primary.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = CardActivateCopy.HERO_BADGE,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = HzColors.Primary,
            )
        }
    }
}

@Composable
private fun CardKeyIconBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.28f),
                        HzColors.BgCard,
                    ),
                ),
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.85f),
                        HzColors.PrimaryDark.copy(alpha = 0.35f),
                        HzColors.Border,
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = HzColors.PrimaryLight,
            modifier = Modifier.size(34.dp),
        )
    }
}

@Composable
fun CardActivateGlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard.copy(alpha = 0.92f),
                        HzColors.BgElevated.copy(alpha = 0.88f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.Primary.copy(alpha = 0.55f),
                        HzColors.Primary.copy(alpha = 0.08f),
                        HzColors.Border,
                    ),
                ),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(18.dp),
    ) {
        content()
    }
}

@Composable
fun CardActivateNoticeStrip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.NoticeBackground)
            .border(1.dp, HzColors.NoticeBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "ⓘ",
            style = MaterialTheme.typography.labelMedium,
            color = HzColors.Warning,
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextSecondary,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
        )
    }
}

@Composable
fun CardActivateTipsCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HzColors.BgCard.copy(alpha = 0.72f))
            .border(1.dp, HzColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = CardActivateCopy.TIPS_TITLE,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = HzColors.TextSecondary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = HzColors.TextMuted,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
        )
    }
}

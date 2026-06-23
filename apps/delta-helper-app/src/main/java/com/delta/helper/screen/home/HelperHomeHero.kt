package com.delta.helper.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors

@Composable
fun HelperHomeHero(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = HelperHomeCopy.BRAND_LABEL,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 2,
                fontWeight = FontWeight.Medium,
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
            style = MaterialTheme.typography.titleMedium,
            color = HzColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(HzColors.NoticeBackground)
                .border(1.dp, HzColors.NoticeBorder, RoundedCornerShape(999.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = HelperHomeCopy.HERO_BADGE,
                style = MaterialTheme.typography.labelMedium,
                color = HzColors.Warning,
            )
        }
    }
}

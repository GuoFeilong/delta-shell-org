package com.delta.helper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HzDarkColorScheme = darkColorScheme(
    primary = HzColors.Primary,
    onPrimary = Color.White,
    primaryContainer = HzColors.PrimaryDark,
    secondary = HzColors.PrimaryLight,
    background = HzColors.BgPrimary,
    onBackground = HzColors.TextPrimary,
    surface = HzColors.BgCard,
    onSurface = HzColors.TextPrimary,
    surfaceVariant = HzColors.BgElevated,
    onSurfaceVariant = HzColors.TextSecondary,
    outline = HzColors.Border,
    error = HzColors.Error,
)

@Composable
fun DeltaHelperTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = HzDarkColorScheme,
        typography = Typography,
        content = content,
    )
}

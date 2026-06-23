package com.delta.helper.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OverlayBackground = Color(0xCC0A0C10)
private val OverlayBorder = Color(0x55FFFFFF)
private val LabelColor = Color(0x99A8B0BC)
private val ValueColor = Color(0xFFE8EAED)
private val AccentColor = Color(0xFF00D4AA)
private val DividerColor = Color(0x33FFFFFF)

@Composable
fun FloatingOverlayPanel(
    session: OverlaySession,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(OverlayBackground, RoundedCornerShape(14.dp))
            .border(1.dp, OverlayBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ADAPTATION PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentColor,
                    ),
                )
                Text(
                    text = session.gameProfile.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = ValueColor.copy(alpha = 0.85f),
                    ),
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss overlay",
                    tint = ValueColor.copy(alpha = 0.7f),
                )
            }
        }

        OverlayDivider()

        OverlayInfoRow(label = "FPS Target", value = session.fpsTarget, highlight = true)
        OverlayInfoRow(label = "Quality Preset", value = session.qualityPreset)
        OverlayInfoRow(label = "Device", value = session.deviceName)
        OverlayInfoRow(label = "Performance", value = session.performanceTier)
        OverlayInfoRow(label = "Display", value = session.displaySpec)
        OverlayInfoRow(label = "Pixel Density", value = session.pixelDensity)
        OverlayInfoRow(label = "Memory", value = session.memorySpec)
        OverlayInfoRow(label = "CPU", value = session.cpuCores)
        OverlayInfoRow(label = "Platform", value = session.platform)
        OverlayInfoRow(label = "Profile Status", value = session.profileStatus)
        OverlayInfoRow(label = "Apply Mode", value = session.renderMode)

        OverlayDivider()

        Text(
            text = "Apply settings manually in-game.",
            style = MaterialTheme.typography.labelSmall.copy(
                color = LabelColor,
                lineHeight = 16.sp,
            ),
        )
    }
}

@Composable
private fun OverlayInfoRow(
    label: String,
    value: String,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.95f),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = LabelColor,
            ),
        )
        Text(
            text = value,
            modifier = Modifier.weight(1.05f),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
                color = if (highlight) AccentColor else ValueColor,
            ),
        )
    }
}

@Composable
private fun OverlayDivider() {
    Text(
        text = "────────────────────────",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            color = DividerColor,
            letterSpacing = 0.sp,
        ),
    )
}

package com.delta.helper.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OverlayBackground = Color(0xCC0A0C10)
private val OverlayBorder = Color(0x55FFFFFF)
private val LabelColor = Color(0x99A8B0BC)
private val ValueColor = Color(0xFFE8EAED)
private val AccentColor = Color(0xFF00D4AA)

@Composable
fun FloatingOverlayPanel(
    session: OverlaySession,
    onDismiss: () -> Unit,
    onDrag: (deltaX: Float, deltaY: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .background(OverlayBackground, RoundedCornerShape(14.dp))
            .border(1.dp, OverlayBorder, RoundedCornerShape(14.dp))
            .padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = session.gameProfile.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentColor,
                ),
            )
            OverlayInfoRow(label = "FPS", value = session.fpsTarget, highlight = true)
            OverlayInfoRow(label = "Resolution", value = session.resolution)
            OverlayInfoRow(label = "Memory", value = session.memory)
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss overlay",
                tint = ValueColor.copy(alpha = 0.7f),
            )
        }
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = LabelColor,
            ),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
                color = if (highlight) AccentColor else ValueColor,
            ),
        )
    }
}

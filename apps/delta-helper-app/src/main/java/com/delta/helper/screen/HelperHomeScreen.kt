package com.delta.helper.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.ui.theme.HzColors

@Composable
fun HelperHomeScreen(modifier: Modifier = Modifier) {
    HelperAdaptiveContainer(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Delta Helper",
                style = MaterialTheme.typography.headlineMedium,
                color = HzColors.TextPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
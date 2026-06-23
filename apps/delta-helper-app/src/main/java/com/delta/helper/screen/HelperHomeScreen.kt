package com.delta.helper.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@androidx.compose.runtime.Composable
fun HelperHomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Delta Helper",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
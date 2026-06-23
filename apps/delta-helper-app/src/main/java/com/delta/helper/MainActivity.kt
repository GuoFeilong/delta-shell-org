package com.delta.helper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.delta.features.activation.ui.ActivationGate
import com.delta.features.activation.ui.ActivationPurchaseUrlEffect
import com.delta.helper.ui.theme.DeltaHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeltaHelperTheme {
                ActivationPurchaseUrlEffect()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ActivationGate(modifier = Modifier.padding(innerPadding)) {
                        HelperHomeScreen(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun HelperHomeScreen(modifier: Modifier = Modifier) {
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

package com.delta.features.activation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ActivationPurchaseUrlEffect(
    viewModel: ActivationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.openPurchaseUrl.collect { url ->
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
            )
        }
    }
}

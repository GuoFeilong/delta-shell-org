package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.delta.helper.screen.card.CardActivateRoute

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
) {
    var activated by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    if (activated) {
        HelperHomeScreen(modifier = modifier.fillMaxSize())
    } else {
        CardActivateRoute(
            modifier = modifier.fillMaxSize(),
            onActivated = { activated = true },
            onBack = { (context as? ComponentActivity)?.finish() },
        )
    }
}

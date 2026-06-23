package com.delta.helper.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.delta.helper.screen.card.CardActivateRoute

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
) {
    var activated by rememberSaveable { mutableStateOf(false) }

    if (activated) {
        HelperHomeScreen(modifier = modifier.fillMaxSize())
    } else {
        CardActivateRoute(
            modifier = modifier.fillMaxSize(),
            onActivated = { activated = true },
        )
    }
}

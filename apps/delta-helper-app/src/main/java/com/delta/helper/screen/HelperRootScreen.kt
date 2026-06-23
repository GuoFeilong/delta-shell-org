package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    BackHandler {
        (context as? ComponentActivity)?.finish()
    }

    HelperHomeScreen(
        modifier = modifier.fillMaxSize(),
        onGameSelected = { /* 游戏详情 / 激活流程待接入 */ },
    )
}

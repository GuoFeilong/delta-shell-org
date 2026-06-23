package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.delta.helper.screen.game.GameDetailRoute
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.gameProfileFor

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedGameId by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler {
        when (selectedGameId) {
            null -> (context as? ComponentActivity)?.finish()
            else -> selectedGameId = null
        }
    }

    when (val gameIdName = selectedGameId) {
        null -> {
            HelperHomeScreen(
                modifier = modifier.fillMaxSize(),
                onGameSelected = { selectedGameId = it.name },
            )
        }

        else -> {
            val game = gameProfileFor(GameId.valueOf(gameIdName))
            GameDetailRoute(
                game = game,
                onBack = { selectedGameId = null },
                modifier = modifier.fillMaxSize(),
            )
        }
    }
}

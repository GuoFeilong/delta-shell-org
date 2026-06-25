package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.delta.helper.screen.card.CardActivateRoute
import com.delta.helper.screen.game.GameDetailRoute
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.gameProfileFor

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedGameId by rememberSaveable { mutableStateOf<String?>(null) }
    var showActivation by rememberSaveable { mutableStateOf(false) }
    var activationLaunchNonce by rememberSaveable { mutableIntStateOf(0) }
    var activationScreenKey by rememberSaveable { mutableIntStateOf(0) }

    fun openActivation() {
        activationScreenKey++
        showActivation = true
    }

    BackHandler {
        when {
            showActivation -> showActivation = false
            selectedGameId != null -> selectedGameId = null
            else -> (context as? ComponentActivity)?.finish()
        }
    }

    when {
        showActivation -> {
            CardActivateRoute(
                viewModelStoreKey = activationScreenKey,
                modifier = modifier.fillMaxSize(),
                onBack = { showActivation = false },
                onActivated = {
                    showActivation = false
                    activationLaunchNonce++
                },
            )
        }

        selectedGameId != null -> {
            val game = gameProfileFor(GameId.valueOf(selectedGameId!!))
            GameDetailRoute(
                game = game,
                modifier = modifier.fillMaxSize(),
                onBack = { selectedGameId = null },
                onRequireActivation = { openActivation() },
                activationLaunchNonce = activationLaunchNonce,
            )
        }

        else -> {
            HelperHomeScreen(
                modifier = modifier.fillMaxSize(),
                onGameSelected = { selectedGameId = it.name },
            )
        }
    }
}

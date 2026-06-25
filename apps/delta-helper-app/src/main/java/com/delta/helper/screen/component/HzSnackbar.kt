package com.delta.helper.screen.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.launch

class HzSnackbarHostState internal constructor(
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    suspend fun showMessage(message: String) {
        if (message.isBlank()) return
        snackbarHostState.showSnackbar(message)
    }
}

@Composable
fun rememberHzSnackbarHostState(): HzSnackbarHostState =
    remember { HzSnackbarHostState() }

val LocalHzSnackbarHostState = compositionLocalOf<HzSnackbarHostState> {
    error("LocalHzSnackbarHostState not provided")
}

@Composable
fun ProvideHzSnackbarHostState(
    hostState: HzSnackbarHostState = rememberHzSnackbarHostState(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalHzSnackbarHostState provides hostState) {
        content()
    }
}

@Composable
fun HzSnackbarHost(
    hostState: HzSnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState.snackbarHostState,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        snackbar = { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(14.dp),
                containerColor = HzColors.BgElevated,
                contentColor = HzColors.TextPrimary,
                actionColor = HzColors.Primary,
                actionContentColor = HzColors.Primary,
                dismissActionContentColor = HzColors.TextMuted,
            )
        },
    )
}

@Composable
fun rememberHzSnackbarShow(): (String) -> Unit {
    val hostState = LocalHzSnackbarHostState.current
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) {
        { message ->
            scope.launch { hostState.showMessage(message) }
        }
    }
}

@Composable
fun HzScaffoldWithSnackbar(
    modifier: Modifier = Modifier,
    snackbarHostState: HzSnackbarHostState = rememberHzSnackbarHostState(),
    content: @Composable (Modifier) -> Unit,
) {
    ProvideHzSnackbarHostState(snackbarHostState) {
        Scaffold(
            modifier = modifier,
            containerColor = HzColors.BgPrimary,
            snackbarHost = { HzSnackbarHost(snackbarHostState) },
            content = { innerPadding -> content(Modifier.padding(innerPadding)) },
        )
    }
}

package com.delta.helper.screen.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.launch

enum class HzSnackbarType {
    Default,
    Error,
    Success,
}

class HzSnackbarHostState internal constructor(
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    var lastType by mutableStateOf(HzSnackbarType.Default)
        private set

    suspend fun showMessage(
        message: String,
        type: HzSnackbarType = HzSnackbarType.Default,
    ) {
        if (message.isBlank()) return
        lastType = type
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

private data class HzSnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionColor: Color,
)

private fun colorsFor(type: HzSnackbarType): HzSnackbarColors =
    when (type) {
        HzSnackbarType.Default -> HzSnackbarColors(
            containerColor = HzColors.BgElevated,
            contentColor = HzColors.TextPrimary,
            actionColor = HzColors.Primary,
        )
        HzSnackbarType.Error -> HzSnackbarColors(
            containerColor = Color(0xFF2E1A1E),
            contentColor = HzColors.TextPrimary,
            actionColor = HzColors.Error,
        )
        HzSnackbarType.Success -> HzSnackbarColors(
            containerColor = Color(0xFF142822),
            contentColor = HzColors.TextPrimary,
            actionColor = HzColors.Primary,
        )
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
            val colors = colorsFor(hostState.lastType)
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(14.dp),
                containerColor = colors.containerColor,
                contentColor = colors.contentColor,
                actionColor = colors.actionColor,
                actionContentColor = colors.actionColor,
                dismissActionContentColor = HzColors.TextMuted,
            )
        },
    )
}

@Composable
fun HzSnackbarMessageEffect(
    message: String?,
    type: HzSnackbarType = HzSnackbarType.Default,
    onConsumed: () -> Unit = {},
) {
    val snackbarHostState = LocalHzSnackbarHostState.current
    LaunchedEffect(message) {
        val text = message?.trim().orEmpty()
        if (text.isNotEmpty()) {
            snackbarHostState.showMessage(text, type)
            onConsumed()
        }
    }
}

@Composable
fun rememberHzSnackbarShow(): (String, HzSnackbarType) -> Unit {
    val hostState = LocalHzSnackbarHostState.current
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) {
        { message, type ->
            scope.launch { hostState.showMessage(message, type) }
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

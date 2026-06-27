package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.helper.screen.consent.FirstLaunchConsentRoute
import com.delta.helper.screen.consent.FirstLaunchConsentViewModel
import com.delta.helper.screen.splash.CustomSplashScreen
import com.delta.helper.screen.component.HzScaffoldWithSnackbar
import com.delta.helper.ui.theme.HzColors

@Composable
fun AppEntryHost(
    modifier: Modifier = Modifier,
) {
    var showSplash by remember { mutableStateOf(true) }
    if (showSplash) {
        CustomSplashScreen(
            onFinished = { showSplash = false },
            modifier = modifier,
        )
        return
    }

    val viewModel: FirstLaunchConsentViewModel = hiltViewModel()
    val consentState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.refreshConsentState()
    }

    HzScaffoldWithSnackbar(modifier = modifier.fillMaxSize()) { innerModifier ->
        when {
            consentState.isLoading -> {
                Box(
                    modifier = innerModifier
                        .fillMaxSize()
                        .background(HzColors.BgPrimary),
                )
            }

            consentState.needsConsent -> {
                FirstLaunchConsentRoute(
                    onDecline = { viewModel.declineAndExit { activity.finishAndRemoveTask() } },
                    modifier = innerModifier,
                    viewModel = viewModel,
                )
            }

            else -> {
                HelperRootScreen(
                    modifier = innerModifier.fillMaxSize(),
                )
            }
        }
    }
}

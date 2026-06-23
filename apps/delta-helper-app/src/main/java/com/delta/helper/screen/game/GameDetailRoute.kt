package com.delta.helper.screen.game

import androidx.activity.ComponentActivity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.helper.overlay.OverlayController
import com.delta.helper.overlay.OverlayLaunchCoordinator
import com.delta.helper.overlay.OverlaySession
import com.delta.helper.screen.component.GameLaunchDock
import com.delta.helper.screen.component.HzDeviceInfoCard
import com.delta.helper.screen.component.HzFpsSettingSection
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.home.GameHeroStrip
import com.delta.helper.screen.home.GameProfileItem
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec

@Composable
fun GameDetailRoute(
    game: GameProfileItem,
    onBack: () -> Unit,
    onRequireActivation: () -> Unit,
    activationLaunchNonce: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: GameDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spec = rememberHelperAdaptiveSpec()
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var pendingOverlaySession by remember { mutableStateOf<OverlaySession?>(null) }

    fun showOverlayAndMinimize(session: OverlaySession) {
        OverlayController.show(context, session)
        activity?.let(OverlayLaunchCoordinator::enterGameAssistMode)
    }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        pendingOverlaySession?.let { session ->
            if (OverlayController.canDrawOverlays(context)) {
                showOverlayAndMinimize(session)
                pendingOverlaySession = null
            } else {
                Toast.makeText(context, "Overlay permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchOverlay(session: OverlaySession) {
        if (OverlayController.canDrawOverlays(context)) {
            showOverlayAndMinimize(session)
        } else {
            pendingOverlaySession = session
            overlayPermissionLauncher.launch(OverlayController.overlayPermissionIntent(context))
        }
    }

    val canLaunch = !uiState.deviceLoading &&
        uiState.deviceInfo != null &&
        !uiState.isCheckingActivation

    LaunchedEffect(game.id) {
        viewModel.initializeForGame(game.id)
        viewModel.loadDeviceInfo()
    }

    LaunchedEffect(activationLaunchNonce) {
        if (activationLaunchNonce > 0) {
            viewModel.launchActivatedSession()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is GameDetailEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is GameDetailEvent.LaunchOverlay -> launchOverlay(event.session)
                GameDetailEvent.NavigateToActivation -> onRequireActivation()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HelperHomeBackground()

        HelperAdaptiveContainer(
            modifier = Modifier.fillMaxSize(),
            spec = spec,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = if (spec.isTabletOrFoldExpanded) 24.dp else 8.dp,
                            bottom = 120.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    HzTopBar(
                        title = game.title,
                        subtitle = game.tagline.uppercase(),
                        showBack = true,
                        onBack = onBack,
                    )

                    GameHeroStrip(game = game)

                    HzDeviceInfoCard(
                        deviceInfo = uiState.deviceInfo,
                        loading = uiState.deviceLoading,
                        errorMessage = uiState.deviceError,
                        accent = game.accent,
                    )

                    HzFpsSettingSection(
                        options = fpsSettingOptions,
                        selectedId = uiState.selectedFpsId,
                        onSelect = viewModel::selectFpsLevel,
                        accent = game.accent,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                GameLaunchDock(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    accent = game.accent,
                    enabled = canLaunch,
                    loading = uiState.isCheckingActivation,
                    onClick = viewModel::onLaunchClick,
                )
            }
        }
    }
}

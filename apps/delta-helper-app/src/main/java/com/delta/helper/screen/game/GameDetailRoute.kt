package com.delta.helper.screen.game

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.helper.overlay.OverlayController
import com.delta.helper.overlay.OverlaySession
import com.delta.helper.screen.component.GameLaunchNoticeHost
import com.delta.helper.screen.component.GameLaunchNoticeSession
import com.delta.helper.screen.component.GameLaunchDock
import com.delta.helper.screen.component.HzConfirmDialog
import com.delta.helper.screen.component.HzDeviceInfoCard
import com.delta.helper.screen.component.HzFpsSettingSection
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.component.LocalHzSnackbarHostState
import com.delta.helper.screen.home.GameHeroStrip
import com.delta.helper.screen.home.GameProfileItem
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import kotlinx.coroutines.launch

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
    val snackbarHostState = LocalHzSnackbarHostState.current
    val scope = rememberCoroutineScope()
    var pendingOverlaySession by remember { mutableStateOf<OverlaySession?>(null) }
    var pendingLaunchAfterPermission by remember { mutableStateOf(false) }
    var showOverlayPermissionDialog by remember { mutableStateOf(false) }
    var launchNoticeSession by remember { mutableStateOf<GameLaunchNoticeSession?>(null) }

    fun onOverlayLaunched() {
        launchNoticeSession = GameLaunchNoticeSession(fpsId = uiState.selectedFpsId)
    }

    fun clearOverlayPermissionPending() {
        pendingOverlaySession = null
        pendingLaunchAfterPermission = false
    }

    val canLaunch = !uiState.deviceLoading &&
        uiState.deviceInfo != null &&
        !uiState.isCheckingActivation

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        when {
            OverlayController.canDrawOverlays(context) -> {
                val session = pendingOverlaySession
                if (session != null) {
                    OverlayController.show(context, session)
                    onOverlayLaunched()
                    clearOverlayPermissionPending()
                } else if (pendingLaunchAfterPermission) {
                    pendingLaunchAfterPermission = false
                    viewModel.onLaunchClick()
                }
            }
            pendingOverlaySession != null || pendingLaunchAfterPermission -> {
                clearOverlayPermissionPending()
                scope.launch {
                    snackbarHostState.showMessage(GameLaunchCopy.OVERLAY_PERMISSION_REQUIRED)
                }
            }
        }
    }

    fun openOverlayPermissionSettings() {
        showOverlayPermissionDialog = false
        overlayPermissionLauncher.launch(OverlayController.overlayPermissionIntent(context))
    }

    fun launchOverlay(session: OverlaySession) {
        if (OverlayController.canDrawOverlays(context)) {
            OverlayController.show(context, session)
            onOverlayLaunched()
        } else {
            pendingOverlaySession = session
            showOverlayPermissionDialog = true
        }
    }

    fun onStartClick() {
        if (!canLaunch) return
        if (OverlayController.canDrawOverlays(context)) {
            viewModel.onLaunchClick()
        } else {
            pendingLaunchAfterPermission = true
            showOverlayPermissionDialog = true
        }
    }

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
                is GameDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showMessage(event.message)
                }
                is GameDetailEvent.LaunchOverlay -> launchOverlay(event.session)
                GameDetailEvent.NavigateToActivation -> onRequireActivation()
            }
        }
    }

    if (showOverlayPermissionDialog) {
        HzConfirmDialog(
            title = GameLaunchCopy.OVERLAY_PERMISSION_TITLE,
            message = GameLaunchCopy.OVERLAY_PERMISSION_MESSAGE,
            confirmText = GameLaunchCopy.OVERLAY_PERMISSION_CONFIRM,
            dismissText = GameLaunchCopy.OVERLAY_PERMISSION_DISMISS,
            onConfirm = ::openOverlayPermissionSettings,
            onDismiss = {
                showOverlayPermissionDialog = false
                clearOverlayPermissionPending()
            },
        )
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
                    onClick = ::onStartClick,
                )

                GameLaunchNoticeHost(
                    session = launchNoticeSession,
                    gameId = game.id,
                    accent = game.accent,
                    onFinished = { launchNoticeSession = null },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 108.dp),
                )
            }
        }
    }
}

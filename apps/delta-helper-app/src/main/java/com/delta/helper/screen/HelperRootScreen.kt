package com.delta.helper.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.core.activation.model.ActivationEntrancePath
import com.delta.helper.activation.ActivationAccessPath
import com.delta.helper.activation.HelperActivationViewModel
import com.delta.helper.screen.card.CardActivateRoute
import com.delta.helper.screen.component.HzConfirmDialog
import com.delta.helper.screen.component.WechatOfficialAccountGuideDialog
import com.delta.helper.screen.game.GameDetailRoute
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.gameProfileFor
import com.delta.helper.screen.task.ActivationEntranceCopy
import com.delta.helper.screen.task.TaskActivateRoute
import kotlinx.coroutines.launch

@Composable
fun HelperRootScreen(
    modifier: Modifier = Modifier,
    activationViewModel: HelperActivationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activationState by activationViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var selectedGameId by rememberSaveable { mutableStateOf<String?>(null) }
    var showActivation by rememberSaveable { mutableStateOf(false) }
    var activationPath by rememberSaveable { mutableStateOf<ActivationAccessPath?>(null) }
    var showEntranceDialog by rememberSaveable { mutableStateOf(false) }
    var entranceHasTask by rememberSaveable { mutableStateOf(false) }
    var activationLaunchNonce by rememberSaveable { mutableIntStateOf(0) }
    var activationScreenKey by rememberSaveable { mutableIntStateOf(0) }
    var showWechatGuideAfterActivation by rememberSaveable { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        activationViewModel.refresh()
    }

    fun openActivationPath(path: ActivationAccessPath) {
        activationPath = path
        activationScreenKey++
        showActivation = true
        showEntranceDialog = false
    }

    fun openActivation() {
        scope.launch {
            val entrance = activationViewModel.resolveEntrance()
            when {
                !entrance.hasAnyVisible -> return@launch
                entrance.cardVisible && entrance.taskVisible -> {
                    entranceHasTask = true
                    showEntranceDialog = true
                }
                entrance.defaultPath == ActivationEntrancePath.CARD || entrance.cardVisible ->
                    openActivationPath(ActivationAccessPath.CARD)
                entrance.defaultPath == ActivationEntrancePath.TASK || entrance.taskVisible ->
                    openActivationPath(ActivationAccessPath.TASK)
            }
        }
    }

    BackHandler {
        when {
            showEntranceDialog -> showEntranceDialog = false
            showActivation -> {
                showActivation = false
                activationPath = null
            }
            selectedGameId != null -> selectedGameId = null
            else -> (context as? ComponentActivity)?.finish()
        }
    }

    if (showEntranceDialog) {
        HzConfirmDialog(
            title = ActivationEntranceCopy.TITLE,
            message = ActivationEntranceCopy.MESSAGE,
            confirmText = ActivationEntranceCopy.CARD_BUTTON,
            dismissText = if (entranceHasTask) {
                ActivationEntranceCopy.TASK_BUTTON
            } else {
                ActivationEntranceCopy.CANCEL_BUTTON
            },
            onConfirm = { openActivationPath(ActivationAccessPath.CARD) },
            onDismiss = {
                if (entranceHasTask) {
                    openActivationPath(ActivationAccessPath.TASK)
                } else {
                    showEntranceDialog = false
                }
            },
        )
    }

    when {
        showActivation && activationPath == ActivationAccessPath.TASK -> {
            TaskActivateRoute(
                viewModelStoreKey = activationScreenKey,
                modifier = modifier.fillMaxSize(),
                onBack = {
                    showActivation = false
                    activationPath = null
                },
                onActivated = {
                    showActivation = false
                    activationPath = null
                    activationLaunchNonce++
                    activationViewModel.refresh()
                    showWechatGuideAfterActivation = true
                },
            )
        }

        showActivation && activationPath == ActivationAccessPath.CARD -> {
            CardActivateRoute(
                viewModelStoreKey = activationScreenKey,
                modifier = modifier.fillMaxSize(),
                onBack = {
                    showActivation = false
                    activationPath = null
                },
                onActivated = {
                    showActivation = false
                    activationPath = null
                    activationLaunchNonce++
                    activationViewModel.refresh()
                    showWechatGuideAfterActivation = true
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
                activationState = activationState,
            )
            if (showWechatGuideAfterActivation) {
                WechatOfficialAccountGuideDialog(
                    onDismiss = {
                        showWechatGuideAfterActivation = false
                    },
                )
            }
        }

        else -> {
            HelperHomeScreen(
                modifier = modifier.fillMaxSize(),
                activationState = activationState,
                onGameSelected = { selectedGameId = it.name },
                onRequireActivation = { openActivation() },
            )
        }
    }
}

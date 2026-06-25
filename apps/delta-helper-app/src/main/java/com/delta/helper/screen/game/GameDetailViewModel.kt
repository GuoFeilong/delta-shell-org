package com.delta.helper.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.helper.device.DeviceInfo
import com.delta.helper.device.DeviceInfoReader
import com.delta.helper.overlay.OverlaySession
import com.delta.helper.overlay.OverlaySessionFactory
import com.delta.helper.screen.card.ActivationCheckPort
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.GameProfileItem
import com.delta.helper.screen.home.gameProfileFor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GameDetailEvent {
    data class LaunchOverlay(val session: OverlaySession) : GameDetailEvent

    data object NavigateToActivation : GameDetailEvent

    data class ShowSnackbar(val message: String) : GameDetailEvent
}

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val deviceInfoReader: DeviceInfoReader,
    private val activationCheckPort: ActivationCheckPort,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameDetailEvent>()
    val events: SharedFlow<GameDetailEvent> = _events.asSharedFlow()

    private var currentGame: GameProfileItem? = null

    fun initializeForGame(gameId: GameId) {
        currentGame = gameProfileFor(gameId)
        _uiState.update {
            it.copy(selectedFpsId = defaultFpsForGame(gameId))
        }
    }

    fun selectFpsLevel(id: String) {
        _uiState.update { it.copy(selectedFpsId = id) }
    }

    fun loadDeviceInfo() {
        if (_uiState.value.deviceLoading || _uiState.value.deviceInfo != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(deviceLoading = true, deviceError = null) }
            runCatching {
                withContext(Dispatchers.Default) { deviceInfoReader.read() }
            }.onSuccess { info ->
                _uiState.update {
                    it.copy(deviceLoading = false, deviceInfo = info)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        deviceLoading = false,
                        deviceError = error.message ?: "设备信息读取失败",
                    )
                }
            }
        }
    }

    fun onLaunchClick() {
        if (_uiState.value.isCheckingActivation) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingActivation = true) }
            val result = activationCheckPort.checkActivation()
            _uiState.update { it.copy(isCheckingActivation = false) }
            when {
                result.isError -> _events.emit(GameDetailEvent.ShowSnackbar(result.message.orEmpty()))
                result.activated -> launchActivatedSession()
                else -> _events.emit(GameDetailEvent.NavigateToActivation)
            }
        }
    }

    fun launchActivatedSession() {
        viewModelScope.launch {
            val game = currentGame ?: return@launch
            val state = _uiState.value
            val deviceInfo = state.deviceInfo
            if (deviceInfo == null) {
                _events.emit(GameDetailEvent.ShowSnackbar("设备信息未就绪"))
                return@launch
            }
            _events.emit(
                GameDetailEvent.LaunchOverlay(
                    OverlaySessionFactory.create(
                        game = game,
                        selectedFpsId = state.selectedFpsId,
                        deviceInfo = deviceInfo,
                    ),
                ),
            )
        }
    }
}

data class GameDetailUiState(
    val deviceLoading: Boolean = false,
    val deviceInfo: DeviceInfo? = null,
    val deviceError: String? = null,
    val selectedFpsId: String = "fps120",
    val isCheckingActivation: Boolean = false,
)

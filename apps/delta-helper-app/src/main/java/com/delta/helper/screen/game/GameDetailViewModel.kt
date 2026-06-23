package com.delta.helper.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.helper.device.DeviceInfo
import com.delta.helper.device.DeviceInfoReader
import com.delta.helper.screen.card.ActivationCheckPort
import com.delta.helper.screen.home.GameId
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
    data class ShowToast(val message: String) : GameDetailEvent

    data object NavigateToActivation : GameDetailEvent
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

    fun initializeForGame(gameId: GameId) {
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
                result.isError -> _events.emit(GameDetailEvent.ShowToast(result.message.orEmpty()))
                result.activated -> _events.emit(
                    GameDetailEvent.ShowToast(result.message.orEmpty()),
                )
                else -> _events.emit(GameDetailEvent.NavigateToActivation)
            }
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

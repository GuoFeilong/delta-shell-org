package com.delta.helper.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.helper.device.DeviceInfo
import com.delta.helper.device.DeviceInfoReader
import com.delta.helper.screen.home.GameId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val deviceInfoReader: DeviceInfoReader,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

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
}

data class GameDetailUiState(
    val deviceLoading: Boolean = false,
    val deviceInfo: DeviceInfo? = null,
    val deviceError: String? = null,
    val selectedFpsId: String = "fps120",
)

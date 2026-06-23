package com.delta.features.activation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.core.activation.model.ActivationDeviceStatus
import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.model.TaskList
import com.delta.core.activation.model.TaskStep
import com.delta.core.activation.repository.ActivationTasksRepository
import com.delta.core.network.model.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TaskActivationViewModel @Inject constructor(
    private val activationTasksRepository: ActivationTasksRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskActivationUiState())
    val uiState: StateFlow<TaskActivationUiState> = _uiState.asStateFlow()

    private val _openUrl = MutableSharedFlow<String>()
    val openUrl = _openUrl.asSharedFlow()

    private var progressToken: String? = null

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            activationTasksRepository.listTasks().collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> applyTaskList(result.data)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAnswerChanged(value: String) {
        _uiState.update { it.copy(answer = value, errorMessage = null) }
    }

    fun onStepCardCodeChanged(value: String) {
        _uiState.update { it.copy(stepCardCode = value, errorMessage = null) }
    }

    fun verifyCurrentStep() {
        val step = _uiState.value.currentStep ?: return
        val answer = _uiState.value.answer.trim()
        if (answer.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入答案") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            activationTasksRepository.verifyStep(step.id, answer).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> handleVerifyResult(result.data)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun redeemStepCard() {
        val step = _uiState.value.currentStep ?: return
        if (!step.stepCardEnabled) {
            _uiState.update {
                it.copy(
                    errorMessage = ActivationErrorCodes.messageFor(
                        ActivationErrorCodes.STEP_CARD_NOT_ENABLED,
                        "当前步骤未开启卡密解锁",
                    ),
                )
            }
            return
        }

        val cardCode = _uiState.value.stepCardCode.trim()
        if (cardCode.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = ActivationErrorCodes.messageFor(
                        ActivationErrorCodes.CARD_CODE_REQUIRED,
                        "卡密不能为空",
                    ),
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            activationTasksRepository.redeemStepCard(step.id, cardCode).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> handleVerifyResult(result.data)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun openLink(url: String?) {
        if (url.isNullOrBlank()) return
        viewModelScope.launch { _openUrl.emit(url) }
    }

    private fun handleVerifyResult(result: com.delta.core.activation.model.TaskVerifyResult) {
        progressToken = result.progressToken
        if (!result.correct) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = "答案不正确，请重试",
                )
            }
            return
        }

        if (result.completed) {
            completeActivation(result.progressToken)
        } else {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    verifiedCount = result.verifiedCount,
                    totalCount = result.totalCount,
                    answer = "",
                    stepCardCode = "",
                    feedbackMessage = "步骤 ${result.verifiedCount}/${result.totalCount} 已完成",
                )
            }
            refreshCurrentStep(result.verifiedCount)
        }
    }

    private fun completeActivation(token: String) {
        viewModelScope.launch {
            activationTasksRepository.completeTasks(token).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> {
                        val activated = result.data.activated &&
                            result.data.status == ActivationDeviceStatus.ACTIVE
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                isLoading = false,
                                isActivated = activated,
                                feedbackMessage = if (activated) {
                                    "任务完成，应用已激活"
                                } else {
                                    "任务完成，但激活状态异常"
                                },
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(
                                    result.code,
                                    result.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun applyTaskList(taskList: TaskList) {
        progressToken = taskList.progressToken
        _uiState.update {
            it.copy(
                isLoading = false,
                steps = taskList.steps,
                verifiedCount = taskList.verifiedCount,
                totalCount = taskList.totalCount,
                currentStep = taskList.currentStep,
                feedbackMessage = if (taskList.totalCount == 0) {
                    "暂无可用任务"
                } else {
                    "任务进度 ${taskList.verifiedCount}/${taskList.totalCount}"
                },
            )
        }
    }

    private fun refreshCurrentStep(verifiedCount: Int) {
        val step = _uiState.value.steps.getOrNull(verifiedCount)
        _uiState.update { it.copy(currentStep = step) }
    }
}

data class TaskActivationUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isActivated: Boolean = false,
    val steps: List<TaskStep> = emptyList(),
    val currentStep: TaskStep? = null,
    val verifiedCount: Int = 0,
    val totalCount: Int = 0,
    val answer: String = "",
    val stepCardCode: String = "",
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
) {
    val canSubmitAnswer: Boolean
        get() = !isSubmitting && !isLoading && currentStep != null && answer.isNotBlank()

    val canRedeemStepCard: Boolean
        get() = !isSubmitting && !isLoading &&
            currentStep?.stepCardEnabled == true &&
            stepCardCode.isNotBlank()
}

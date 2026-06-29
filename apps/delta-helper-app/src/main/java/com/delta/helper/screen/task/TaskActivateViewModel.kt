package com.delta.helper.screen.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.core.activation.model.ActivationDeviceStatus
import com.delta.core.activation.model.ActivationErrorCodes
import com.delta.core.activation.model.TaskStep
import com.delta.core.activation.model.TaskVerifyResult
import com.delta.core.activation.repository.ActivationTasksRepository
import com.delta.core.network.model.ApiResult
import com.delta.helper.activation.DeviceProfileSynchronizer
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
class TaskActivateViewModel @Inject constructor(
    private val activationTasksRepository: ActivationTasksRepository,
    private val deviceProfileSynchronizer: DeviceProfileSynchronizer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskActivateUiState())
    val uiState: StateFlow<TaskActivateUiState> = _uiState.asStateFlow()

    private val _openUrl = MutableSharedFlow<String>()
    val openUrl = _openUrl.asSharedFlow()

    private var progressToken: String? = null
    private var taskStack: ArrayDeque<TaskStep> = ArrayDeque()

    init {
        deviceProfileSynchronizer.scheduleSync()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            activationTasksRepository.listTasks().collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> applyTaskList(result.data.steps, result.data.verifiedCount, result.data.totalCount, result.data.progressToken)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = ActivationErrorCodes.messageFor(result.code, result.message),
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

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun submitAnswer() {
        val step = _uiState.value.currentStep ?: return
        val answer = _uiState.value.answer.trim()
        if (answer.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入当前任务的答案") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            activationTasksRepository.verifyStep(step.id, answer).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> handleStepProgress(result.data, requireCorrect = true)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(result.code, result.message),
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
                it.copy(errorMessage = ActivationErrorCodes.messageFor(
                    ActivationErrorCodes.STEP_CARD_NOT_ENABLED,
                    "当前步骤未开启卡密解锁",
                ))
            }
            return
        }
        val cardCode = _uiState.value.stepCardCode.trim()
        if (cardCode.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入当前步骤卡密") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            activationTasksRepository.redeemStepCard(step.id, cardCode).collect { result ->
                when (result) {
                    ApiResult.Loading -> Unit
                    is ApiResult.Success -> handleStepProgress(result.data, requireCorrect = false)
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(result.code, result.message),
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

    private fun handleStepProgress(result: TaskVerifyResult, requireCorrect: Boolean) {
        progressToken = result.progressToken
        if (requireCorrect && !result.correct) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    answer = "",
                    errorMessage = "答案不对，请继续完成任务",
                )
            }
            return
        }

        if (result.completed) {
            completeActivation(result.progressToken)
            return
        }

        if (taskStack.isEmpty()) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = "请刷新任务列表",
                )
            }
            return
        }

        val remaining = taskStack.size
        val nextStep = taskStack.removeFirst()
        _uiState.update {
            it.copy(
                isSubmitting = false,
                verifiedCount = result.verifiedCount,
                totalCount = result.totalCount,
                currentStep = nextStep,
                answer = "",
                stepCardCode = "",
                successMessage = if (requireCorrect) {
                    "回答正确，还剩 $remaining 题"
                } else {
                    "当前步骤已解锁，还剩 $remaining 题"
                },
            )
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
                                activationSuccessToken = if (activated) {
                                    it.activationSuccessToken + 1
                                } else {
                                    it.activationSuccessToken
                                },
                                successMessage = if (activated) {
                                    TaskActivateCopy.ACTIVATION_SUCCESS
                                } else {
                                    null
                                },
                                errorMessage = if (activated) null else "任务完成，但激活状态异常",
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = ActivationErrorCodes.messageFor(result.code, result.message),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun applyTaskList(
        steps: List<TaskStep>,
        verifiedCount: Int,
        totalCount: Int,
        token: String?,
    ) {
        progressToken = token
        taskStack = buildPendingTaskStack(steps, verifiedCount)
        val current = taskStack.removeFirstOrNull()

        if (current == null && totalCount > 0 && verifiedCount >= totalCount && !token.isNullOrBlank()) {
            completeActivation(token)
            return
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                steps = steps,
                verifiedCount = verifiedCount,
                totalCount = totalCount,
                currentStep = current,
                errorMessage = if (steps.isEmpty()) TaskActivateCopy.EMPTY_TASKS else null,
            )
        }
    }
}

data class TaskActivateUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isActivated: Boolean = false,
    val steps: List<TaskStep> = emptyList(),
    val currentStep: TaskStep? = null,
    val verifiedCount: Int = 0,
    val totalCount: Int = 0,
    val answer: String = "",
    val stepCardCode: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val activationSuccessToken: Long = 0L,
) {
    val hasTasks: Boolean get() = totalCount > 0

    val progressCurrent: Int
        get() {
            val total = totalCount.coerceAtLeast(1)
            val done = verifiedCount.coerceAtLeast(0)
            return (done + if (currentStep != null) 1 else 0).coerceAtMost(total)
        }

    val progressPercent: Float
        get() {
            val total = totalCount.coerceAtLeast(1)
            return (verifiedCount.coerceAtLeast(0).toFloat() / total).coerceIn(0f, 1f)
        }

    val heartenText: String
        get() = currentStep?.encourageText?.takeIf { it.isNotBlank() }
            ?: TaskActivateCopy.DEFAULT_HEARTEN

    val canSubmit: Boolean
        get() = !isSubmitting && !isLoading && currentStep != null && answer.isNotBlank()

    val canRedeemStepCard: Boolean
        get() = !isSubmitting && !isLoading &&
            currentStep?.stepCardEnabled == true &&
            stepCardCode.isNotBlank()
}

package com.delta.helper.screen.task

import com.delta.core.activation.model.TaskStep

/**
 * 对齐 HZGS `buildPendingTaskStack`：按 sortOrder 排序，跳过已验证步，栈顶为当前待做任务。
 */
internal fun buildPendingTaskStack(
    steps: List<TaskStep>,
    verifiedCount: Int,
): ArrayDeque<TaskStep> {
    if (steps.isEmpty()) return ArrayDeque()

    val sorted = steps.sortedBy { it.sortOrder }
    val pending = sorted.drop(verifiedCount.coerceAtMost(sorted.size))
    return ArrayDeque(pending)
}

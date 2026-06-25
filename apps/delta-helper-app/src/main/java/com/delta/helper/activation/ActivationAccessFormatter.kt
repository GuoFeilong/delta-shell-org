package com.delta.helper.activation

import com.delta.core.activation.model.ActivationSource
import com.delta.core.activation.model.ActivationStatus

object ActivationAccessFormatter {
    private val planLabels = mapOf(
        "MONTH" to "月卡",
        "QUARTER" to "季卡",
        "YEAR" to "年卡",
        "LIFETIME" to "永久",
    )

    private val sourceLabels = mapOf(
        ActivationSource.CARD to "卡密激活",
        ActivationSource.TASK to "任务开通",
    )

    fun formatPlanLabel(planCode: String?): String {
        val code = planCode?.trim().orEmpty()
        if (code.isEmpty()) return ""
        return planLabels[code.uppercase()] ?: code
    }

    fun formatSource(source: ActivationSource?): String =
        source?.let { sourceLabels[it] } ?: ""

    fun formatAccessExpiry(status: ActivationStatus): String {
        val plan = formatPlanLabel(status.planCode)
        if (status.accessExpiresAt.isNullOrBlank() && status.planCode.equals("LIFETIME", ignoreCase = true)) {
            return if (plan.isNotEmpty()) "$plan · 永久有效" else "永久有效"
        }
        status.daysRemaining?.takeIf { it >= 0 }?.let { days ->
            return if (plan.isNotEmpty()) "$plan · 剩余 $days 天" else "剩余 $days 天"
        }
        status.accessExpiresAt?.takeIf { it.isNotBlank() }?.let { expiresAt ->
            val date = expiresAt.replace('T', ' ').take(10)
            return if (plan.isNotEmpty()) "$plan · 至 $date" else "至 $date"
        }
        return plan
    }

    fun formatStatusSummary(status: ActivationStatus): String {
        val parts = buildList {
            formatSource(status.source).takeIf { it.isNotEmpty() }?.let(::add)
            formatAccessExpiry(status).takeIf { it.isNotEmpty() }?.let(::add)
        }
        return parts.joinToString(" · ")
    }
}

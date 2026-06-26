package com.delta.helper.activation

import androidx.compose.ui.graphics.Color
import com.delta.helper.ui.theme.HzColors

/**
 * 已开通横幅按卡密规格区分强调色（与 HzPlanPicker 尊享/推荐层级呼应）。
 */
data class ActivationPlanVisualTheme(
    val accent: Color,
    val planLabel: String?,
    val showPlanBadge: Boolean,
) {
    val borderColor: Color get() = accent.copy(alpha = 0.38f)
    val backgroundTint: Color get() = accent.copy(alpha = 0.1f)
    val secondaryTint: Color get() = accent.copy(alpha = 0.05f)
}

object ActivationPlanVisualThemes {
    fun resolve(planCode: String?, activated: Boolean): ActivationPlanVisualTheme {
        if (!activated) {
            return ActivationPlanVisualTheme(
                accent = HzColors.Warning,
                planLabel = null,
                showPlanBadge = false,
            )
        }
        return when (normalizePlanCode(planCode)) {
            "MONTH" -> ActivationPlanVisualTheme(
                accent = HzColors.PlanMonth,
                planLabel = "月卡",
                showPlanBadge = true,
            )
            "QUARTER" -> ActivationPlanVisualTheme(
                accent = HzColors.Primary,
                planLabel = "季卡",
                showPlanBadge = true,
            )
            "YEAR" -> ActivationPlanVisualTheme(
                accent = HzColors.PlanYear,
                planLabel = "年卡",
                showPlanBadge = true,
            )
            "LIFETIME" -> ActivationPlanVisualTheme(
                accent = HzColors.PlanLifetime,
                planLabel = "永久",
                showPlanBadge = true,
            )
            else -> ActivationPlanVisualTheme(
                accent = HzColors.PrimaryLight,
                planLabel = null,
                showPlanBadge = false,
            )
        }
    }

    private fun normalizePlanCode(planCode: String?): String? =
        planCode?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
}

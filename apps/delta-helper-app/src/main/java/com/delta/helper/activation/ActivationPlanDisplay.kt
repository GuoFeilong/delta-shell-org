package com.delta.helper.activation

import com.delta.helper.screen.card.CardPurchaseOptionUi

data class ActivationPlanDisplay(
    val title: String,
    val subtitle: String,
    val priceDisplay: String?,
    val isLifetime: Boolean,
    val isDefault: Boolean,
)

object ActivationPlanDisplayFormatter {
    private val subtitles = mapOf(
        "MONTH" to "30 天内容访问",
        "QUARTER" to "90 天内容访问",
        "YEAR" to "365 天内容访问",
        "LIFETIME" to "长期有效，不限时长",
    )

    fun format(option: CardPurchaseOptionUi): ActivationPlanDisplay {
        val planCode = option.planCode.trim().uppercase()
        val title = ActivationAccessFormatter.formatPlanLabel(planCode)
            .ifBlank { option.label.trim() }
        val mappedSubtitle = subtitles[planCode].orEmpty()
        val subtitle = when {
            mappedSubtitle.isNotEmpty() -> mappedSubtitle
            option.label.isNotBlank() && option.label != title -> option.label
            else -> ""
        }
        return ActivationPlanDisplay(
            title = title,
            subtitle = subtitle,
            priceDisplay = option.priceDisplay?.trim()?.takeIf { it.isNotEmpty() },
            isLifetime = planCode == "LIFETIME",
            isDefault = option.default,
        )
    }
}

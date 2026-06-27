package com.delta.helper.activation

import com.delta.helper.screen.card.CardPurchaseOptionUi

data class ActivationPlanDisplay(
    val planCode: String,
    val title: String,
    val subtitle: String,
    val priceDisplay: String?,
    val originalPriceDisplay: String? = null,
    val savingsDisplay: String? = null,
    val isLifetime: Boolean,
    val isDefault: Boolean,
    /** 营销角标，如永久卡「限时4折」 */
    val promoBadge: String? = null,
    /** 转化向卖点，展示在副标题下方 */
    val valueProposition: String? = null,
) {
    val hasPromo: Boolean
        get() = !originalPriceDisplay.isNullOrBlank() || !savingsDisplay.isNullOrBlank()

    val isMonth: Boolean
        get() = planCode == "MONTH"

    val isYear: Boolean
        get() = planCode == "YEAR"
}

object ActivationPlanDisplayFormatter {
    /** 永久卡限时促销；活动结束改为 false 即可隐藏 */
    private const val LIFETIME_PROMO_ENABLED = true
    private const val LIFETIME_PROMO_LABEL = "限时折扣"

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
            planCode = planCode,
            title = title,
            subtitle = subtitle,
            priceDisplay = option.priceDisplay?.trim()?.takeIf { it.isNotEmpty() },
            originalPriceDisplay = option.originalPriceDisplay?.trim()?.takeIf { it.isNotEmpty() }
                ?.takeUnless { it == option.priceDisplay?.trim() },
            savingsDisplay = option.savingsDisplay?.trim()?.takeIf { it.isNotEmpty() },
            isLifetime = planCode == "LIFETIME",
            isDefault = option.default,
            promoBadge = if (planCode == "LIFETIME" && LIFETIME_PROMO_ENABLED) LIFETIME_PROMO_LABEL else null,
            valueProposition = valuePropositionFor(planCode, option),
        )
    }

    private fun valuePropositionFor(planCode: String, option: CardPurchaseOptionUi): String? = when (planCode) {
        "MONTH" -> null
        "QUARTER" -> "灵活续费，随用随买"
        "YEAR" -> when {
            option.default -> "推荐 · 性价比最高"
            option.originalPriceDisplay != null -> "限时特惠，省更多"
            else -> "适合长期参考内容访问"
        }
        "LIFETIME" -> when {
            option.savingsDisplay != null -> "一次购买，长期可用"
            else -> "尊享永久访问权限"
        }
        else -> null
    }
}

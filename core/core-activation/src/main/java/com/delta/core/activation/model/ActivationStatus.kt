package com.delta.core.activation.model

data class ActivationStatus(
    val activated: Boolean,
    val status: ActivationDeviceStatus,
    val source: ActivationSource?,
    val publisherKey: String?,
    val clientOs: String?,
    val clientChannel: String?,
    val activatedAt: String?,
    val appPackageName: String?,
)

enum class ActivationDeviceStatus {
    INACTIVE,
    ACTIVE,
    REVOKED,
    ;

    companion object {
        fun from(raw: String): ActivationDeviceStatus =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: INACTIVE
    }
}

enum class ActivationSource {
    CARD,
    TASK,
    ;

    companion object {
        fun from(raw: String?): ActivationSource? =
            raw?.let { value ->
                entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
            }
    }
}

data class CardPurchaseUrl(
    val purchaseUrl: String?,
)

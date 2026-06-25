package com.delta.helper.screen.card

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceholderCardActivationPort @Inject constructor() : CardActivationPort {
    override suspend fun fetchPurchaseOptions(): List<CardPurchaseOption> = emptyList()

    override suspend fun fetchPurchaseUrl(): String? = null

    override suspend fun isActivationGateEnabled(): Boolean = false

    override suspend fun redeemCard(cardCode: String): CardActivationOutcome =
        CardActivationOutcome.Failure(
            message = "激活接口待接入（已预留 CardActivationPort）",
        )
}

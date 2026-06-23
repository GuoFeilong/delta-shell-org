package com.delta.helper.screen.card

import com.delta.helper.BuildConfig
import com.delta.helper.activation.LocalActivationSession
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Daily 本地调试：默认视为未激活，卡密兑换成功后写入 [LocalActivationSession]，
 * 再点 START 时视为已激活；online 构建走真实服务端状态。
 */
@Singleton
class DevActivationCheckPort @Inject constructor(
    private val remote: RemoteActivationCheckPort,
    private val localSession: LocalActivationSession,
) : ActivationCheckPort {
    override suspend fun checkActivation(): ActivationCheckResult {
        if (BuildConfig.MOCK_ALREADY_ACTIVATED) {
            return ActivationCheckResult(
                activated = true,
                message = RemoteCardActivationPort.ACTIVATED_MESSAGE,
            )
        }
        if (!BuildConfig.SIMULATE_NOT_ACTIVATED) {
            return remote.checkActivation()
        }
        if (localSession.isLocallyActivated()) {
            return ActivationCheckResult(
                activated = true,
                message = RemoteCardActivationPort.ACTIVATED_MESSAGE,
            )
        }
        return ActivationCheckResult(activated = false)
    }
}

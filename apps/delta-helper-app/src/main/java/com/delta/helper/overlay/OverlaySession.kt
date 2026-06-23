package com.delta.helper.overlay

import android.content.Intent
import com.delta.helper.device.DeviceInfo
import com.delta.helper.screen.game.fpsLabelEnglish
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.GameProfileItem

data class OverlaySession(
    val gameProfile: String,
    val fpsTarget: String,
    val resolution: String,
    val memory: String,
) {
    fun writeTo(intent: Intent) {
        intent.apply {
            putExtra(Extra.GAME_PROFILE, gameProfile)
            putExtra(Extra.FPS_TARGET, fpsTarget)
            putExtra(Extra.RESOLUTION, resolution)
            putExtra(Extra.MEMORY, memory)
        }
    }

    companion object {
        object Extra {
            const val GAME_PROFILE = "game_profile"
            const val FPS_TARGET = "fps_target"
            const val RESOLUTION = "resolution"
            const val MEMORY = "memory"
        }

        fun from(intent: Intent): OverlaySession? {
            val gameProfile = intent.getStringExtra(Extra.GAME_PROFILE) ?: return null
            return OverlaySession(
                gameProfile = gameProfile,
                fpsTarget = intent.getStringExtra(Extra.FPS_TARGET).orEmpty(),
                resolution = intent.getStringExtra(Extra.RESOLUTION).orEmpty(),
                memory = intent.getStringExtra(Extra.MEMORY).orEmpty(),
            )
        }
    }
}

object OverlaySessionFactory {
    fun create(
        game: GameProfileItem,
        selectedFpsId: String,
        deviceInfo: DeviceInfo,
    ): OverlaySession = OverlaySession(
        gameProfile = game.englishProfileName(),
        fpsTarget = fpsLabelEnglish(selectedFpsId),
        resolution = deviceInfo.screen,
        memory = deviceInfo.memorySizeGb ?: "N/A",
    )
}

fun GameProfileItem.englishProfileName(): String = when (id) {
    GameId.Delta -> "Delta Force"
    GameId.ArenaBreakout -> "Arena Breakout"
    GameId.PeaceElite -> "Peace Elite"
    GameId.CallOfDuty -> "Call of Duty Mobile"
}

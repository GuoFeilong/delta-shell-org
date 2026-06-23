package com.delta.helper.overlay

import android.content.Intent
import com.delta.helper.device.DeviceInfo
import com.delta.helper.screen.game.fpsLabelEnglish
import com.delta.helper.screen.game.qualityPresetEnglish
import com.delta.helper.screen.home.GameId
import com.delta.helper.screen.home.GameProfileItem

data class OverlaySession(
    val gameProfile: String,
    val gameCode: String,
    val fpsTarget: String,
    val qualityPreset: String,
    val deviceName: String,
    val performanceTier: String,
    val displaySpec: String,
    val pixelDensity: String,
    val memorySpec: String,
    val cpuCores: String,
    val platform: String,
    val profileStatus: String = "ACTIVE",
    val renderMode: String = "MANUAL APPLY",
) {
    fun writeTo(intent: Intent) {
        intent.apply {
            putExtra(Extra.GAME_PROFILE, gameProfile)
            putExtra(Extra.GAME_CODE, gameCode)
            putExtra(Extra.FPS_TARGET, fpsTarget)
            putExtra(Extra.QUALITY_PRESET, qualityPreset)
            putExtra(Extra.DEVICE_NAME, deviceName)
            putExtra(Extra.PERFORMANCE_TIER, performanceTier)
            putExtra(Extra.DISPLAY_SPEC, displaySpec)
            putExtra(Extra.PIXEL_DENSITY, pixelDensity)
            putExtra(Extra.MEMORY_SPEC, memorySpec)
            putExtra(Extra.CPU_CORES, cpuCores)
            putExtra(Extra.PLATFORM, platform)
            putExtra(Extra.PROFILE_STATUS, profileStatus)
            putExtra(Extra.RENDER_MODE, renderMode)
        }
    }

    companion object {
        object Extra {
            const val GAME_PROFILE = "game_profile"
            const val GAME_CODE = "game_code"
            const val FPS_TARGET = "fps_target"
            const val QUALITY_PRESET = "quality_preset"
            const val DEVICE_NAME = "device_name"
            const val PERFORMANCE_TIER = "performance_tier"
            const val DISPLAY_SPEC = "display_spec"
            const val PIXEL_DENSITY = "pixel_density"
            const val MEMORY_SPEC = "memory_spec"
            const val CPU_CORES = "cpu_cores"
            const val PLATFORM = "platform"
            const val PROFILE_STATUS = "profile_status"
            const val RENDER_MODE = "render_mode"
        }

        fun from(intent: Intent): OverlaySession? {
            val gameProfile = intent.getStringExtra(Extra.GAME_PROFILE) ?: return null
            return OverlaySession(
                gameProfile = gameProfile,
                gameCode = intent.getStringExtra(Extra.GAME_CODE).orEmpty(),
                fpsTarget = intent.getStringExtra(Extra.FPS_TARGET).orEmpty(),
                qualityPreset = intent.getStringExtra(Extra.QUALITY_PRESET).orEmpty(),
                deviceName = intent.getStringExtra(Extra.DEVICE_NAME).orEmpty(),
                performanceTier = intent.getStringExtra(Extra.PERFORMANCE_TIER).orEmpty(),
                displaySpec = intent.getStringExtra(Extra.DISPLAY_SPEC).orEmpty(),
                pixelDensity = intent.getStringExtra(Extra.PIXEL_DENSITY).orEmpty(),
                memorySpec = intent.getStringExtra(Extra.MEMORY_SPEC).orEmpty(),
                cpuCores = intent.getStringExtra(Extra.CPU_CORES).orEmpty(),
                platform = intent.getStringExtra(Extra.PLATFORM).orEmpty(),
                profileStatus = intent.getStringExtra(Extra.PROFILE_STATUS) ?: "ACTIVE",
                renderMode = intent.getStringExtra(Extra.RENDER_MODE) ?: "MANUAL APPLY",
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
        gameCode = game.id.name.uppercase(),
        fpsTarget = fpsLabelEnglish(selectedFpsId),
        qualityPreset = qualityPresetEnglish(selectedFpsId),
        deviceName = "${deviceInfo.brand} ${deviceInfo.model}".trim(),
        performanceTier = deviceInfo.performanceTier.englishLabel,
        displaySpec = deviceInfo.screen,
        pixelDensity = deviceInfo.pixelRatio,
        memorySpec = deviceInfo.memorySizeGb ?: "N/A",
        cpuCores = "${deviceInfo.cpuCores} cores",
        platform = deviceInfo.system,
    )
}

fun GameProfileItem.englishProfileName(): String = when (id) {
    GameId.Delta -> "Delta Force"
    GameId.ArenaBreakout -> "Arena Breakout"
    GameId.PeaceElite -> "Peace Elite"
    GameId.CallOfDuty -> "Call of Duty Mobile"
}

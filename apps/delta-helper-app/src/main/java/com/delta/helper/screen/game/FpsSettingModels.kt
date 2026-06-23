package com.delta.helper.screen.game

import com.delta.helper.screen.home.GameId

data class FpsOption(
    val id: String,
    val label: String,
    val isHighFps: Boolean = false,
)

object FpsSettingCopy {
    const val SECTION_TITLE = "画质参考"
    const val FPS_TITLE = "目标帧率（参考）"
    const val FPS_HINT =
        "以下为参考档位；实际帧率取决于设备性能与游戏内设置，需您在游戏中手动调整"
    const val PREVIEW_NOTE =
        "本 App 不会修改游戏客户端；请进入游戏后对照方案手动设置，我们不保证达到所选帧率"
}

val fpsSettingOptions: List<FpsOption> = listOf(
    FpsOption(id = "fps90", label = "90帧"),
    FpsOption(id = "fps120", label = "120帧"),
    FpsOption(id = "fps144", label = "144帧", isHighFps = true),
    FpsOption(id = "fps165", label = "165帧", isHighFps = true),
)

fun defaultFpsForGame(gameId: GameId): String = when (gameId) {
    GameId.Delta -> "fps120"
    GameId.ArenaBreakout -> "fps90"
    GameId.PeaceElite -> "fps90"
    GameId.CallOfDuty -> "fps120"
}

fun fpsLabelEnglish(fpsId: String): String = when (fpsId) {
    "fps90" -> "90 FPS"
    "fps120" -> "120 FPS"
    "fps144" -> "144 FPS"
    "fps165" -> "165 FPS"
    else -> fpsId.uppercase()
}

fun qualityPresetEnglish(fpsId: String): String = when (fpsId) {
    "fps165", "fps144" -> "ULTRA"
    "fps120" -> "HIGH"
    "fps90" -> "BALANCED"
    else -> "STANDARD"
}

fun fpsNumericDisplay(fpsId: String): String = when (fpsId) {
    "fps90" -> "90"
    "fps120" -> "120"
    "fps144" -> "144"
    "fps165" -> "165"
    else -> fpsId.removePrefix("fps")
}

fun fpsLaunchNoticeMessage(fpsId: String): String {
    val fps = fpsNumericDisplay(fpsId)
    return "已加载 ${fps} 帧参考方案，请进入游戏后按方案手动设置"
}

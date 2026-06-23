package com.delta.helper.screen.game

import com.delta.helper.screen.home.GameId

data class FpsOption(
    val id: String,
    val label: String,
    val isHighFps: Boolean = false,
)

object FpsSettingCopy {
    const val SECTION_TITLE = "画质设置"
    const val FPS_TITLE = "帧数设置"
    const val FPS_HINT = "提高可增加画面流畅度；若运行卡顿、发热或耗电量过大，可适当降低"
    const val PREVIEW_NOTE = "以下为参考方案预览，需在对应游戏内手动调整设置"
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

fun fpsActivatedMessage(fpsId: String): String {
    val fps = fpsNumericDisplay(fpsId)
    return "${fps} 帧已开启，即将打开游戏"
}

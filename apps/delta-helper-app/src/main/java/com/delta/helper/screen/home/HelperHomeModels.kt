package com.delta.helper.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.delta.helper.R

enum class GameId {
    Delta,
    ArenaBreakout,
    PeaceElite,
    CallOfDuty,
}

data class GameProfileItem(
    val id: GameId,
    val title: String,
    val tagline: String,
    @DrawableRes val iconRes: Int,
    val accent: Color,
    val accentGlow: Color,
)

object HelperHomeCopy {
    const val BRAND_LABEL = "GRAPHICS MONSTER"
    const val PAGE_TITLE = "画质怪兽"
    const val PAGE_SUBTITLE = "游戏画质与帧率参考工具"
    const val HERO_BADGE = "144 / 165 帧参考方案库（非效果承诺）"
    const val FOOTNOTE = "请选择要查看参考方案的游戏（144 / 165 帧档位仅供参考）"
    const val CARD_QUALITY_LABEL = "高画质参考方案"
    const val FPS_144_LABEL = "144 FPS"
    const val FPS_165_LABEL = "165 FPS"
}

val helperHomeGames: List<GameProfileItem> = listOf(
    GameProfileItem(
        id = GameId.Delta,
        title = "三角洲画质",
        tagline = "Delta Force · 战术竞技",
        iconRes = R.drawable.ic_game_delta,
        accent = Color(0xFF00D4AA),
        accentGlow = Color(0x6600D4AA),
    ),
    GameProfileItem(
        id = GameId.ArenaBreakout,
        title = "暗区突围画质",
        tagline = "Arena Breakout · 硬核撤离",
        iconRes = R.drawable.ic_game_arena_breakout,
        accent = Color(0xFFFF8C42),
        accentGlow = Color(0x66FF8C42),
    ),
    GameProfileItem(
        id = GameId.PeaceElite,
        title = "和平精英画质",
        tagline = "Peace Elite · 经典吃鸡",
        iconRes = R.drawable.ic_game_peace_elite,
        accent = Color(0xFFFFD166),
        accentGlow = Color(0x66FFD166),
    ),
    GameProfileItem(
        id = GameId.CallOfDuty,
        title = "使命召唤画质",
        tagline = "Call of Duty · 快节奏 FPS",
        iconRes = R.drawable.ic_game_call_of_duty,
        accent = Color(0xFF4DA6FF),
        accentGlow = Color(0x664DA6FF),
    ),
)

fun gameProfileFor(id: GameId): GameProfileItem =
    helperHomeGames.first { it.id == id }

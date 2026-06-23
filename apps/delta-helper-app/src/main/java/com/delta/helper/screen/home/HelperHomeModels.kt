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
    const val PAGE_SUBTITLE = "专注于游戏画质提升"
    const val HERO_BADGE = "手游最高画质 · 144 / 165 FPS 方案库"
    const val FOOTNOTE = "请选择您想改的游戏画质（最高支持 144 / 165 帧率）"
    const val CARD_QUALITY_LABEL = "最高画质"
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

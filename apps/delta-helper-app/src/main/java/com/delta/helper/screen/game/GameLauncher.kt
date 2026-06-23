package com.delta.helper.screen.game

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.delta.helper.screen.home.GameId

object GameLauncher {
    data class LaunchResult(
        val launched: Boolean,
        val notInstalledMessage: String? = null,
    )

    fun launch(context: Context, gameId: GameId): LaunchResult {
        val pm = context.packageManager
        for (packageName in packageNamesFor(gameId)) {
            val launchIntent = resolveLauncherIntent(pm, packageName) ?: continue
            return try {
                startGameActivity(context, launchIntent)
                LaunchResult(launched = true)
            } catch (_: ActivityNotFoundException) {
                continue
            }
        }
        return LaunchResult(
            launched = false,
            notInstalledMessage = notInstalledMessage(gameId),
        )
    }

    fun notInstalledMessage(gameId: GameId): String =
        "未检测到${displayName(gameId)}，请先安装游戏"

    private fun startGameActivity(context: Context, intent: Intent) {
        if (context is Activity) {
            val activity = context
            activity.window.decorView.post {
                activity.moveTaskToBack(true)
                activity.window.decorView.post {
                    activity.startActivity(intent)
                }
            }
        } else {
            context.startActivity(
                Intent(intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    /** 与桌面图标一致的 MAIN/LAUNCHER 入口，避免 getLaunchIntent + NEW_TASK 导致部分游戏黑屏。 */
    private fun resolveLauncherIntent(pm: PackageManager, packageName: String): Intent? {
        if (!isPackageInstalled(pm, packageName)) return null

        val queryIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(packageName)
        }
        val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                queryIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        val launcherActivity = activities.maxByOrNull { it.priority }?.activityInfo
            ?: return pm.getLaunchIntentForPackage(packageName)?.apply {
                flags = flags and Intent.FLAG_ACTIVITY_NEW_TASK.inv()
                addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }

        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(launcherActivity.packageName, launcherActivity.name)
            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean =
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
        }.isSuccess

    private fun displayName(gameId: GameId): String = when (gameId) {
        GameId.Delta -> "三角洲行动"
        GameId.ArenaBreakout -> "暗区突围"
        GameId.PeaceElite -> "和平精英"
        GameId.CallOfDuty -> "使命召唤手游"
    }

    /** 国服常见包名；按顺序尝试，兼容渠道差异。 */
    private fun packageNamesFor(gameId: GameId): List<String> = when (gameId) {
        GameId.Delta -> listOf("com.tencent.tmgp.dfm")
        GameId.ArenaBreakout -> listOf(
            "com.tencent.mf.uam",
            "com.proximabeta.mf.uamo",
        )
        GameId.PeaceElite -> listOf("com.tencent.tmgp.pubgmhd")
        GameId.CallOfDuty -> listOf("com.tencent.tmgp.cod")
    }
}

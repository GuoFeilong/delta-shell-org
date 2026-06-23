package com.delta.helper.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class DeviceInfoReader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun read(): DeviceInfo {
        val metrics = context.resources.displayMetrics
        val memoryGb = readTotalMemoryGb()
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val performanceScore = estimatePerformanceScore(memoryGb, cpuCores)
        val tier = DevicePerformanceTier.fromScore(performanceScore)

        return DeviceInfo(
            brand = formatBrand(Build.BRAND, Build.MANUFACTURER),
            model = formatModel(Build.MODEL),
            platformLabel = "Android",
            system = "Android ${Build.VERSION.RELEASE}",
            screen = "${metrics.widthPixels} × ${metrics.heightPixels}",
            pixelRatio = formatPixelRatio(metrics),
            performanceLabel = tier.label,
            memorySizeGb = memoryGb?.let { formatMemoryGb(it) },
            cpuCores = cpuCores,
        )
    }

    private fun readTotalMemoryGb(): Double? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return null
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
    }

    private fun estimatePerformanceScore(memoryGb: Double?, cpuCores: Int): Int {
        val ramScore = when {
            memoryGb == null -> 15
            memoryGb >= 11 -> 38
            memoryGb >= 8 -> 32
            memoryGb >= 6 -> 26
            memoryGb >= 4 -> 18
            memoryGb >= 3 -> 12
            else -> 6
        }
        val coreBonus = (cpuCores - 4).coerceIn(0, 8)
        val apiBonus = when {
            Build.VERSION.SDK_INT >= 34 -> 4
            Build.VERSION.SDK_INT >= 31 -> 2
            else -> 0
        }
        return ramScore + coreBonus + apiBonus
    }

    private fun formatBrand(brand: String, manufacturer: String): String {
        val raw = brand.takeIf { it.isNotBlank() && it != "unknown" }
            ?: manufacturer.takeIf { it.isNotBlank() && it != "unknown" }
            ?: "未知品牌"
        return raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun formatModel(model: String): String {
        if (model.isBlank()) return "未知型号"
        return model.trim()
    }

    private fun formatPixelRatio(metrics: DisplayMetrics): String {
        val ratio = metrics.density
        val formatted = if (ratio % 1f == 0f) {
            ratio.toInt().toString()
        } else {
            String.format("%.1f", ratio)
        }
        return "${formatted}x"
    }

    private fun formatMemoryGb(gb: Double): String {
        val rounded = when {
            gb >= 10.5 -> gb.roundToInt()
            gb >= 1 -> ((gb * 2).roundToInt() / 2.0).let { half ->
                if (half % 1.0 == 0.0) half.toInt().toString() else half.toString()
            }
            else -> gb.toString()
        }
        return "$rounded GB"
    }
}

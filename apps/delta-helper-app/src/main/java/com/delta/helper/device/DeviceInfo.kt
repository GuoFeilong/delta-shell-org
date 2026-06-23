package com.delta.helper.device

data class DeviceInfo(
    val brand: String,
    val model: String,
    val platformLabel: String,
    val system: String,
    val screen: String,
    val pixelRatio: String,
    val performanceLabel: String,
    val performanceTier: DevicePerformanceTier,
    val memorySizeGb: String?,
    val cpuCores: Int,
)

enum class DevicePerformanceTier(val label: String, val englishLabel: String) {
    Flagship("旗舰", "Flagship"),
    HighEnd("高端", "High-End"),
    MidRange("中端", "Mid-Range"),
    Entry("入门", "Entry"),
    Low("较低", "Low"),
    Unknown("未知", "Unknown"),
    ;

    companion object {
        fun fromScore(score: Int): DevicePerformanceTier = when {
            score >= 40 -> Flagship
            score >= 30 -> HighEnd
            score >= 20 -> MidRange
            score >= 10 -> Entry
            score >= 0 -> Low
            else -> Unknown
        }
    }
}

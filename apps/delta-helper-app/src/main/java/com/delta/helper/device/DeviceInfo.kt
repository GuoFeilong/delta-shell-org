package com.delta.helper.device

data class DeviceInfo(
    val brand: String,
    val model: String,
    val platformLabel: String,
    val system: String,
    val screen: String,
    val pixelRatio: String,
    val performanceLabel: String,
    val memorySizeGb: String?,
    val cpuCores: Int,
)

enum class DevicePerformanceTier(val label: String) {
    Flagship("旗舰"),
    HighEnd("高端"),
    MidRange("中端"),
    Entry("入门"),
    Low("较低"),
    Unknown("未知"),
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

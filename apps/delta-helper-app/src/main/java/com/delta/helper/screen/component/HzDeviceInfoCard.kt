package com.delta.helper.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.device.DeviceInfo
import com.delta.helper.ui.theme.HzColors

@Composable
fun HzDeviceInfoCard(
    deviceInfo: DeviceInfo?,
    loading: Boolean,
    errorMessage: String?,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        HzColors.BgCard,
                        accent.copy(alpha = 0.06f),
                    ),
                ),
            )
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "📱", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "设备信息",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = HzColors.TextPrimary,
                        ),
                    )
                }
                HzStatusTag(
                    text = when {
                        loading -> "识别中"
                        deviceInfo != null -> "已读取"
                        else -> "读取失败"
                    },
                    accent = accent,
                    highlighted = deviceInfo != null && !loading,
                )
            }

            when {
                loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 12.dp),
                            color = accent,
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "正在读取设备信息...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HzColors.TextSecondary,
                        )
                    }
                }

                deviceInfo != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = deviceInfo.model,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HzColors.TextPrimary,
                            ),
                        )
                        Text(
                            text = "${deviceInfo.platformLabel} · ${deviceInfo.brand} · ${deviceInfo.system}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HzColors.TextSecondary,
                        )
                    }

                    DeviceSpecGrid(deviceInfo = deviceInfo, accent = accent)
                }

                else -> {
                    Text(
                        text = errorMessage ?: "暂时无法读取本机信息，请稍后重试",
                        style = MaterialTheme.typography.bodySmall,
                        color = HzColors.Error,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceSpecGrid(
    deviceInfo: DeviceInfo,
    accent: Color,
) {
    val specs = buildList {
        add("屏幕" to deviceInfo.screen)
        add("像素比" to deviceInfo.pixelRatio)
        add("性能" to deviceInfo.performanceLabel)
        if (deviceInfo.memorySizeGb != null) {
            add("内存" to deviceInfo.memorySizeGb)
        } else {
            add("核心" to "${deviceInfo.cpuCores} 核")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        specs.chunked(2).forEach { rowSpecs ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowSpecs.forEach { (label, value) ->
                    DeviceSpecCell(
                        label = label,
                        value = value,
                        accent = accent,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowSpecs.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DeviceSpecCell(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HzColors.BgInput)
            .border(1.dp, HzColors.Border.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = HzColors.TextMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (label == "性能") accent else HzColors.TextPrimary,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HzStatusTag(
    text: String,
    accent: Color,
    highlighted: Boolean,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (highlighted) accent.copy(alpha = 0.16f) else HzColors.BgInput,
            )
            .border(
                width = 1.dp,
                color = if (highlighted) accent.copy(alpha = 0.45f) else HzColors.Border,
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
            color = if (highlighted) accent else HzColors.TextMuted,
        ),
    )
}

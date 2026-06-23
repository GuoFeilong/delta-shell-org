package com.delta.helper.screen.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class HelperWindowSize {
    Compact,
    Medium,
    Expanded,
}

data class HelperAdaptiveSpec(
    val windowSize: HelperWindowSize,
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val isTabletOrFoldExpanded: Boolean,
)

@Composable
fun rememberHelperAdaptiveSpec(): HelperAdaptiveSpec {
    val containerWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    return remember(containerWidth) {
        when {
            containerWidth >= 840.dp -> HelperAdaptiveSpec(
                windowSize = HelperWindowSize.Expanded,
                horizontalPadding = 48.dp,
                contentMaxWidth = 560.dp,
                isTabletOrFoldExpanded = true,
            )
            containerWidth >= 600.dp -> HelperAdaptiveSpec(
                windowSize = HelperWindowSize.Medium,
                horizontalPadding = 32.dp,
                contentMaxWidth = 520.dp,
                isTabletOrFoldExpanded = true,
            )
            else -> HelperAdaptiveSpec(
                windowSize = HelperWindowSize.Compact,
                horizontalPadding = 16.dp,
                contentMaxWidth = Dp.Unspecified,
                isTabletOrFoldExpanded = false,
            )
        }
    }
}

@Composable
fun HelperAdaptiveContainer(
    modifier: Modifier = Modifier,
    spec: HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (spec.contentMaxWidth != Dp.Unspecified) {
                        Modifier.widthIn(max = spec.contentMaxWidth)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = spec.horizontalPadding)
                .fillMaxSize(),
            content = content,
        )
    }
}

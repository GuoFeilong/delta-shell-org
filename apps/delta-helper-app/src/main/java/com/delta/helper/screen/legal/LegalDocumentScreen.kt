package com.delta.helper.screen.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delta.helper.screen.component.HzNoticeBanner
import com.delta.helper.screen.component.HzTopBar
import com.delta.helper.screen.home.HelperHomeBackground
import com.delta.helper.screen.layout.HelperAdaptiveContainer
import com.delta.helper.screen.layout.HelperAdaptiveSpec
import com.delta.helper.screen.layout.rememberHelperAdaptiveSpec
import com.delta.helper.ui.theme.HzColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LegalDocumentScreen(
    initialType: LegalDocType,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    spec: HelperAdaptiveSpec = rememberHelperAdaptiveSpec(),
) {
    var docType by rememberSaveable(initialType) { mutableStateOf(initialType) }
    val meta = LegalCopy.docMeta.getValue(docType)
    val sections = LegalCopy.document(docType)
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        HelperHomeBackground()

        HelperAdaptiveContainer(
            modifier = Modifier.fillMaxSize(),
            spec = spec,
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = if (spec.isTabletOrFoldExpanded) 24.dp else 8.dp,
                    bottom = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HzTopBar(
                title = meta.title,
                subtitle = meta.subtitle,
                showBack = true,
                onBack = onBack,
            )

            HzNoticeBanner(text = LegalCopy.PRODUCT_NATURE_SHORT)

            sections.forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = section.heading,
                        style = MaterialTheme.typography.titleMedium,
                        color = HzColors.TextPrimary,
                    )
                    section.paragraphs.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium,
                            color = HzColors.TextSecondary,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HzColors.BgCard)
                    .border(1.dp, HzColors.Border, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "相关文档",
                    style = MaterialTheme.typography.labelMedium,
                    color = HzColors.TextMuted,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LegalCopy.relatedLinks(docType).forEach { (type, label) ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    HzColors.InputCardBorder,
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    docType = type
                                    scope.launch { scrollState.scrollTo(0) }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = HzColors.Primary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "更新日期：${LegalCopy.UPDATED_AT} · 协议版本 ${LegalCopy.AGREEMENT_VERSION}",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall,
                color = HzColors.TextMuted,
                textAlign = TextAlign.Center,
            )
        }
        }
    }
}

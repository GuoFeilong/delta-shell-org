package com.delta.helper.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.delta.helper.R
import com.delta.helper.screen.promo.WechatGuideCopy
import com.delta.helper.ui.theme.HzColors
import com.delta.helper.util.GalleryImageSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WechatOfficialAccountGuideDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackbarHostState = LocalHzSnackbarHostState.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    fun dismissAfterSave() {
        scope.launch {
            if (isSaving) return@launch
            isSaving = true
            val saved = withContext(Dispatchers.IO) {
                GalleryImageSaver.saveDrawableToPictures(
                    context = context,
                    drawableResId = R.drawable.img_wechat_official_account_guide,
                    displayName = WechatGuideCopy.ALBUM_FILE_NAME,
                )
            }
            isSaving = false
            onDismiss()
            if (saved) {
                snackbarHostState.showMessage(
                    message = WechatGuideCopy.SAVED_TO_ALBUM,
                    type = HzSnackbarType.Success,
                )
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = {
            if (!isSaving) dismissAfterSave()
        },
        modifier = modifier,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isSaving,
            dismissOnClickOutside = !isSaving,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = HzColors.BgCard,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = WechatGuideCopy.DIALOG_TITLE,
                    style = MaterialTheme.typography.titleLarge,
                    color = HzColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = WechatGuideCopy.DIALOG_SUBTITLE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HzColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Image(
                    painter = painterResource(R.drawable.img_wechat_official_account_guide),
                    contentDescription = WechatGuideCopy.IMAGE_CONTENT_DESCRIPTION,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                )

                HzPrimaryButton(
                    text = if (isSaving) WechatGuideCopy.SAVING_BUTTON else WechatGuideCopy.DISMISS_BUTTON,
                    onClick = ::dismissAfterSave,
                    enabled = !isSaving,
                    loading = isSaving,
                )
            }
        }
    }
}

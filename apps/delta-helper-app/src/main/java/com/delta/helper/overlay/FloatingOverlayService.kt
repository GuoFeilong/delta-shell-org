package com.delta.helper.overlay

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.delta.helper.ui.theme.DeltaHelperTheme
import kotlin.math.roundToInt

class FloatingOverlayService :
    LifecycleService(),
    SavedStateRegistryOwner,
    ViewModelStoreOwner {

    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    private var overlayLayoutParams: WindowManager.LayoutParams? = null

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val overlayViewModelStore = ViewModelStore()

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = overlayViewModelStore

    override fun onCreate() {
        savedStateRegistryController.performRestore(null)
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            OverlayController.ACTION_HIDE -> {
                removeOverlay()
                stopSelf()
            }
            OverlayController.ACTION_SHOW -> {
                val session = intent.let(OverlaySession::from)
                if (session == null) {
                    stopSelf()
                } else {
                    showOverlay(session)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        removeOverlay()
        overlayViewModelStore.clear()
        super.onDestroy()
    }

    private fun showOverlay(session: OverlaySession) {
        removeOverlay()
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dpToPx(OVERLAY_MARGIN_X_DP)
            y = dpToPx(OVERLAY_MARGIN_Y_DP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        overlayLayoutParams = layoutParams

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingOverlayService)
            setViewTreeSavedStateRegistryOwner(this@FloatingOverlayService)
            setViewTreeViewModelStoreOwner(this@FloatingOverlayService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                DeltaHelperTheme {
                    FloatingOverlayPanel(
                        session = session,
                        onDismiss = {
                            OverlayController.hide(this@FloatingOverlayService)
                        },
                        onDrag = { deltaX, deltaY ->
                            updateOverlayPosition(deltaX, deltaY)
                        },
                    )
                }
            }
        }
        windowManager.addView(composeView, layoutParams)
        overlayView = composeView
        OverlayLaunchCoordinator.markOverlayVisible(true)
    }

    private fun updateOverlayPosition(deltaX: Float, deltaY: Float) {
        val view = overlayView ?: return
        val params = overlayLayoutParams ?: return
        params.x += deltaX.roundToInt()
        params.y += deltaY.roundToInt()
        windowManager.updateViewLayout(view, params)
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            runCatching { windowManager.removeView(view) }
            view.disposeComposition()
        }
        overlayView = null
        overlayLayoutParams = null
        OverlayLaunchCoordinator.markOverlayVisible(false)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).roundToInt()

    companion object {
        private const val OVERLAY_MARGIN_X_DP = 16
        private const val OVERLAY_MARGIN_Y_DP = 24
    }
}

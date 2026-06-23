package com.delta.helper.overlay

object OverlayLaunchCoordinator {
    @Volatile
    var isOverlayVisible: Boolean = false
        private set

    internal fun markOverlayVisible(visible: Boolean) {
        isOverlayVisible = visible
    }
}

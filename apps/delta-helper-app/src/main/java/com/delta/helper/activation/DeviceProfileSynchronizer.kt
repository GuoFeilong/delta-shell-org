package com.delta.helper.activation

import com.delta.core.activation.repository.ActivationRepository
import com.delta.core.activation.repository.syncDeviceProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Uploads device profile headers via `/activation/status` on app start and other entry points.
 */
@Singleton
class DeviceProfileSynchronizer @Inject constructor(
    private val activationRepository: ActivationRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun scheduleSync() {
        scope.launch {
            runCatching { activationRepository.syncDeviceProfile() }
        }
    }
}

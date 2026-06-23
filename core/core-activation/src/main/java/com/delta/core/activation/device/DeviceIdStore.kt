package com.delta.core.activation.device

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val Context.deviceIdDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "delta_device_id",
)

private val DeviceIdKey = stringPreferencesKey("device_id")

/**
 * Provides a stable device ID for activation APIs.
 *
 * First launch: fetch Google Play **App Set ID** (privacy-compliant, lint-safe);
 * fall back to UUID when Play services is unavailable. The resolved value is written
 * to DataStore and reused on every subsequent launch.
 *
 * Reinstall clears DataStore → App Set ID is fetched again. With developer scope,
 * the same device often receives the same App Set ID after reinstall, which helps
 * restore backend activation state.
 */
class DeviceIdStore(
    context: Context,
) : DeviceIdProvider {
    private val appContext = context.applicationContext
    private val dataStore = appContext.deviceIdDataStore
    private val appSetIdFetcher = AppSetIdFetcher(appContext)
    private val mutex = Mutex()
    private var cached: String? = null

    override suspend fun getDeviceId(): String {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return it }
            val stored = dataStore.data.first()[DeviceIdKey]
            if (stored != null) {
                cached = stored
                return stored
            }
            val appSetId = appSetIdFetcher.fetchOrNull()
            val created = DeviceIdGenerator.resolve(appSetId)
            dataStore.edit { preferences ->
                preferences[DeviceIdKey] = created
            }
            cached = created
            created
        }
    }
}

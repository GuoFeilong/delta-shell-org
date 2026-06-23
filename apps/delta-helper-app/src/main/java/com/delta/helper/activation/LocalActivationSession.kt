package com.delta.helper.activation

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Application.activationSessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "helper_activation_session",
)

@Singleton
class LocalActivationSession @Inject constructor(
    application: Application,
) {
    private val dataStore = application.activationSessionDataStore

    suspend fun isLocallyActivated(): Boolean =
        dataStore.data.map { it[KEY_LOCALLY_ACTIVATED] ?: false }.first()

    suspend fun markLocallyActivated() {
        dataStore.edit { it[KEY_LOCALLY_ACTIVATED] = true }
    }

    private companion object {
        val KEY_LOCALLY_ACTIVATED = booleanPreferencesKey("locally_activated")
    }
}

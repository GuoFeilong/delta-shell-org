package com.delta.helper.legal

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.delta.helper.screen.legal.LegalCopy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Application.legalConsentDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "helper_legal_consent",
)

@Singleton
class LegalConsentRepository @Inject constructor(
    application: Application,
) {
    private val dataStore = application.legalConsentDataStore

    suspend fun hasValidConsent(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[KEY_ACCEPTED] == true &&
            preferences[KEY_VERSION] == LegalCopy.AGREEMENT_VERSION
    }

    suspend fun acceptConsent() {
        dataStore.edit { preferences ->
            preferences[KEY_ACCEPTED] = true
            preferences[KEY_VERSION] = LegalCopy.AGREEMENT_VERSION
            preferences[KEY_ACCEPTED_AT] = System.currentTimeMillis()
        }
    }

    suspend fun clearConsent() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCEPTED)
            preferences.remove(KEY_VERSION)
            preferences.remove(KEY_ACCEPTED_AT)
        }
    }

    private companion object {
        val KEY_ACCEPTED = booleanPreferencesKey("legal_consent_accepted")
        val KEY_VERSION = stringPreferencesKey("legal_consent_version")
        val KEY_ACCEPTED_AT = longPreferencesKey("legal_consent_at")
    }
}

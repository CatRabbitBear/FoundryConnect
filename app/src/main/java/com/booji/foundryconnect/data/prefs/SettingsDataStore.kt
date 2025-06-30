package com.booji.foundryconnect.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/**
 * Simple DataStore wrapper for persisting API configuration.
 */
class SettingsDataStore(private val context: Context) {

    /** Flow of the stored Azure project id. */
    val projectId: Flow<String> = context.settingsDataStore.data.map { it[KEY_PROJECT] ?: "" }

    /** Flow of the stored model name. */
    val modelName: Flow<String> = context.settingsDataStore.data.map { it[KEY_MODEL] ?: "" }

    /** Flow of the stored API key. */
    val apiKey: Flow<String> = context.settingsDataStore.data.map { it[KEY_KEY] ?: "" }

    /** Persist all values in DataStore. */
    suspend fun save(project: String, model: String, key: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_PROJECT] = project
            prefs[KEY_MODEL] = model
            prefs[KEY_KEY] = key
        }
    }

    private companion object {
        val KEY_PROJECT = stringPreferencesKey("project_id")
        val KEY_MODEL = stringPreferencesKey("model_name")
        val KEY_KEY = stringPreferencesKey("api_key")
    }
}

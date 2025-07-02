package com.booji.foundryconnect.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

    /** Flow of the stored service id. */
    val serviceId: Flow<String> = context.settingsDataStore.data.map { it[KEY_SERVICE_ID] ?: "" }

    /** Flow of the stored API key. */
    val apiKey: Flow<String> = context.settingsDataStore.data.map { it[KEY_KEY] ?: "" }

    /** Flow of the configured max tokens for API requests. */
    val maxTokens: Flow<Int> = context.settingsDataStore.data.map { it[KEY_MAX_TOKENS] ?: 600 }

    /** Flow of the history word limit used when sending requests. */
    val historyWords: Flow<Int> = context.settingsDataStore.data.map { it[KEY_HISTORY_WORDS] ?: 3000 }

    /** Flow of the optional system prompt. */
    val systemMessage: Flow<String> = context.settingsDataStore.data.map { it[KEY_SYSTEM_MESSAGE] ?: "" }

    /** Persist all values in DataStore. */
    suspend fun save(
        project: String,
        model: String,
        key: String,
        maxTokens: Int,
        historyWords: Int,
        systemMessage: String,
        serviceId: String
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[KEY_PROJECT] = project
            prefs[KEY_MODEL] = model
            prefs[KEY_KEY] = key
            prefs[KEY_MAX_TOKENS] = maxTokens
            prefs[KEY_HISTORY_WORDS] = historyWords
            prefs[KEY_SYSTEM_MESSAGE] = systemMessage
            prefs[KEY_SERVICE_ID] = serviceId
        }
    }

    private companion object {
        val KEY_PROJECT = stringPreferencesKey("project_id")
        val KEY_MODEL = stringPreferencesKey("model_name")
        val KEY_KEY = stringPreferencesKey("api_key")
        val KEY_MAX_TOKENS = intPreferencesKey("max_tokens")
        val KEY_HISTORY_WORDS = intPreferencesKey("history_words")
        val KEY_SYSTEM_MESSAGE = stringPreferencesKey("system_message")
        val KEY_SERVICE_ID = stringPreferencesKey("service_id")
    }
}

package com.booji.foundryconnect.data.history

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.chatDataStore by preferencesDataStore(name = "chat_history")

/**
 * DataStore wrapper for persisting recent chat conversations.
 */
class ChatHistoryStore(private val context: Context) {

    private val gson = Gson()
    private val KEY_CHATS = stringPreferencesKey("chats_json")

    /** Flow of all stored chats, newest last. */
    val chats: Flow<List<ChatRecord>> = context.chatDataStore.data.map { prefs ->
        prefs[KEY_CHATS]?.let { json ->
            try {
                gson.fromJson(json, Array<ChatRecord>::class.java)?.toList() ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    /** Persist the given chat, trimming history to [MAX_CHATS]. */
    suspend fun saveChat(record: ChatRecord) {
        context.chatDataStore.edit { prefs ->
            val current = prefs[KEY_CHATS]
            val list = if (!current.isNullOrBlank()) {
                try {
                    gson.fromJson(current, Array<ChatRecord>::class.java)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) { mutableListOf() }
            } else mutableListOf()
            list.removeAll { it.id == record.id }
            list.add(record)
            if (list.size > MAX_CHATS) {
                repeat(list.size - MAX_CHATS) { list.removeAt(0) }
            }
            prefs[KEY_CHATS] = gson.toJson(list)
        }
    }

    /** Remove a chat by id. */
    suspend fun deleteChat(id: String) {
        context.chatDataStore.edit { prefs ->
            val current = prefs[KEY_CHATS]
            if (!current.isNullOrBlank()) {
                val list = try {
                    gson.fromJson(current, Array<ChatRecord>::class.java)?.toMutableList() ?: mutableListOf()
                } catch (_: Exception) { mutableListOf() }
                list.removeAll { it.id == id }
                prefs[KEY_CHATS] = gson.toJson(list)
            }
        }
    }

    private companion object {
        const val MAX_CHATS = 10
    }
}

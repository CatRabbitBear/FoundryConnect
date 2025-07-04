package com.booji.foundryconnect.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booji.foundryconnect.BuildConfig
import com.booji.foundryconnect.data.network.SemanticKernelService
import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.data.repository.ChatRepository
import com.booji.foundryconnect.data.history.ChatHistoryStore
import com.booji.foundryconnect.data.history.ChatRecord
import com.booji.foundryconnect.data.history.HistoryReducer
import com.booji.foundryconnect.data.prefs.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Simple ViewModel holding chat state for the UI layer.
 *
 * Currently this only stores messages typed by the user and responses
 * that will later come from the repository. Networking will be wired
 * in a future sprint.
 */
class ChatViewModel(
    private val repository: ChatRepository = defaultRepository(),
    private val historyStore: ChatHistoryStore? = null,
    private val settingsStore: SettingsDataStore? = null
) : ViewModel() {

    /** Unique id for the active chat conversation. */
    private var currentChatId: String = UUID.randomUUID().toString()

    /** List of chat messages shown in the UI. */
    val messages = mutableStateListOf<Message>()

    /** Current text in the input field. */
    var inputText by mutableStateOf("")

    /** Flag indicating an in-flight network request. */
    var isLoading by mutableStateOf(false)

    /** Holds the latest error message, if any. */
    var errorMessage by mutableStateOf<String?>(null)

    /** Begin a completely new chat session. */
    fun startNewChat() {
        currentChatId = UUID.randomUUID().toString()
        messages.clear()
        inputText = ""
        errorMessage = null
    }

    /** Ensure the current chat is stored if it has content. */
    fun persistChatIfNeeded() {
        if (messages.isEmpty()) return
        viewModelScope.launch {
            historyStore?.saveChat(ChatRecord(currentChatId, messages.toList()))
        }
    }

    /** Load an existing chat into the UI. */
    fun loadChat(record: ChatRecord) {
        currentChatId = record.id
        messages.clear()
        messages += record.messages
        inputText = ""
        errorMessage = null
    }

    /**
     * Sends a user message to the repository and updates UI state based on the
     * response. Any error text returned from the repository is surfaced via
     * [errorMessage] instead of being added to the chat transcript.
     */
    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = Message(role = "user", content = prompt)
        val conversation = messages + userMessage
        messages += userMessage
        inputText = ""

        viewModelScope.launch {
            isLoading = true
            val tokens = settingsStore?.maxTokens?.first() ?: 256
            val limit = settingsStore?.historyWords?.first() ?: 1000
            val system = settingsStore?.systemMessage?.first() ?: ""
            val context = HistoryReducer.buildContext(conversation, system, limit)
            val reply = repository.sendMessage(context, tokens)
            isLoading = false

            if (reply.startsWith("Error")) {
                errorMessage = reply
            } else {
                messages += Message(role = "assistant", content = reply)
                errorMessage = null
            }

            historyStore?.saveChat(ChatRecord(currentChatId, messages.toList()))
        }
    }

    companion object {
        /** Builds a default [ChatRepository] from [BuildConfig] values. */
        fun defaultRepository(): ChatRepository = createRepository(
            BuildConfig.AZURE_PROJECT,
            BuildConfig.AZURE_MODEL,
            BuildConfig.AZURE_API_KEY,
            BuildConfig.SERP_API_KEY,
            BuildConfig.FIRECRAWL_API_KEY
        )
    }
}

/**
 * Helper to construct a [ChatRepository] from runtime settings.
 */
fun createRepository(
    project: String,
    model: String,
    apiKey: String,
    searchKey: String,
    firecrawlApiKey: String
): ChatRepository {
    val backend = SemanticKernelService(project, model, apiKey, searchKey, firecrawlApiKey)
    return ChatRepository(backend)
}


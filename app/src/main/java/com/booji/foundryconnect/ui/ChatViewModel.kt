package com.booji.foundryconnect.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.booji.foundryconnect.data.network.Message

/**
 * Simple ViewModel holding chat state for the UI layer.
 *
 * Currently this only stores messages typed by the user and responses
 * that will later come from the repository. Networking will be wired
 * in a future sprint.
 */
class ChatViewModel : ViewModel() {

    /** List of chat messages shown in the UI. */
    val messages = mutableStateListOf<Message>()

    /** Current text in the input field. */
    var inputText by mutableStateOf("")

    /** Flag indicating an in-flight network request. */
    var isLoading by mutableStateOf(false)

    /** Holds the latest error message, if any. */
    var errorMessage by mutableStateOf<String?>(null)

    /**
     * Sends a user message. This is a stub that simply echoes the prompt back
     * as an assistant response. Real network integration will be added later.
     */
    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        messages += Message(role = "user", content = prompt)
        inputText = ""

        // Placeholder loading flow
        isLoading = true
        // Fake immediate "response" for previewing purposes
        messages += Message(role = "assistant", content = "Echo: $prompt")
        isLoading = false
        errorMessage = null
    }
}

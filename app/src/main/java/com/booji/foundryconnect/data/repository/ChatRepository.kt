package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.ChatBackend
import com.booji.foundryconnect.data.network.Message


/**
 * Gateway between UI and Azure Foundry API.
 *
 * Decision notes:
 *  - Error handling is delegated to the provided [ChatBackend] implementation.
 */
class ChatRepository(
    private val backend: ChatBackend
) {
    suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String {
        return backend.sendMessage(messages, maxTokens)
    }

}

package com.booji.foundryconnect.data.network

/**
 * Generic backend abstraction for sending chat messages.
 */
interface ChatBackend {
    suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String
}

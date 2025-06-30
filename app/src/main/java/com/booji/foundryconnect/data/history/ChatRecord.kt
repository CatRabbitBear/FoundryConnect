package com.booji.foundryconnect.data.history

import com.booji.foundryconnect.data.network.Message

/**
 * Simple container for a stored chat conversation.
 */
data class ChatRecord(
    val id: String,
    val messages: List<Message>
)

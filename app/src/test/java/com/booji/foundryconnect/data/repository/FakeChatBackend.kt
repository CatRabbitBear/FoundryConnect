package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.ChatBackend
import com.booji.foundryconnect.data.network.Message

class FakeChatBackend(private val result: String) : ChatBackend {
    override suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String = result
}

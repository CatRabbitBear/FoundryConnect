package com.booji.foundryconnect.data.history

import com.booji.foundryconnect.data.network.Message

/**
 * Utility for trimming chat history before sending to the API.
 */
object HistoryReducer {
    /**
     * Builds a request context containing [systemMessage] and the most recent
     * messages up to [maxWords]. Entire messages are kept; older messages are
     * dropped when the limit is exceeded.
     */
    fun buildContext(
        history: List<Message>,
        systemMessage: String,
        maxWords: Int
    ): List<Message> {
        val result = mutableListOf<Message>()
        if (systemMessage.isNotBlank()) {
            result += Message(role = "system", content = systemMessage)
        }

        var wordCount = 0
        val trimmed = mutableListOf<Message>()
        for (msg in history.asReversed()) {
            val count = msg.content.trim().split(Regex("\\s+")).size
            if (wordCount + count > maxWords) break
            trimmed.add(0, msg)
            wordCount += count
        }
        if (trimmed.size < history.size) {
            trimmed.add(0, Message("user", "Some old messages removed from context..."))
        }
        result.addAll(trimmed)
        return result
    }
}

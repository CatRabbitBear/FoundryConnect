package com.booji.foundryconnect.data.repository

import android.util.Log
import com.booji.foundryconnect.data.network.FoundryApiService
import com.booji.foundryconnect.data.network.FoundryRequest
import com.booji.foundryconnect.data.network.Message

/**
 * Repository for handling chat logic and Azure Foundry API communications.
 */
class ChatRepository(private val apiService: FoundryApiService) {
    /**
     * Sends a chat prompt to Azure Foundry and returns the assistant's reply.
     * Errors are logged and surfaced as a simple error message string.
     */
    suspend fun sendMessage(prompt: String): String {
        val request = FoundryRequest(messages = listOf(Message(role = "user", content = prompt)))

        return try {
            val response = apiService.sendMessage(request)
            response.choices.firstOrNull()?.message?.content.orEmpty()
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message", e)
            "Error: ${e.message}"
        }
    }
}

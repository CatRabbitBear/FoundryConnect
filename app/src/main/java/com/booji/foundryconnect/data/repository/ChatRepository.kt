package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.FoundryApiService

/**
 * Repository for handling chat logic and Azure Foundry API communications.
 */
class ChatRepository(private val apiService: FoundryApiService) {
    /**
     * Sends a chat prompt to Azure Foundry, handling success and errors gracefully.
     *
     * TODO(Codex):
     * - Make a network request using apiService.sendMessage().
     * - Implement proper error handling (try-catch), logging, and fallback behavior.
     * - Return just the text reply extracted from the FoundryResponse.
     */
    suspend fun sendMessage(prompt: String): String {
        // TODO: Implement network call and response parsing
        return "response from Azure Foundry"
    }
}
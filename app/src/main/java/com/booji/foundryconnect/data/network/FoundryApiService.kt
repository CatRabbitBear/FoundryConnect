package com.booji.foundryconnect.data.network

import retrofit2.http.Body
import retrofit2.http.POST

interface FoundryApiService {
    /**
     * Sends a user's prompt to the Azure Foundry Chat Completions API
     * and retrieves the AI-generated response.
     *
     * TODO(Codex): Implement the request and response data classes:
     * - FoundryRequest should include prompt text and required API fields.
     * - FoundryResponse should parse and contain the AI-generated text reply clearly.
     */
    @POST("chat/completions")
    suspend fun sendMessage(
        @Body request: FoundryRequest
    ): FoundryResponse
}

// TODO: Define FoundryRequest and FoundryResponse data classes
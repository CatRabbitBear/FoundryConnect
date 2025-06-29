package com.booji.foundryconnect.data.network

import retrofit2.http.Body
import retrofit2.http.POST

/** Retrofit API interface for communicating with Azure Foundry. */
interface FoundryApiService {

    /**
     * Sends the user's prompt to the chat completions endpoint and returns the
     * parsed response.
     */
    @POST("chat/completions")
    suspend fun sendMessage(
        @Body request: FoundryRequest
    ): FoundryResponse
}

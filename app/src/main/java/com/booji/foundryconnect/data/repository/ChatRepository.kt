package com.booji.foundryconnect.data.repository

import android.util.Log
import com.booji.foundryconnect.data.network.FoundryApiService
import com.booji.foundryconnect.data.network.FoundryRequest
import com.booji.foundryconnect.data.network.Message
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Gateway between UI and Azure Foundry API.
 *
 * Decision notes:
 *  - We wrap our Retrofit call in a try/catch to handle network or parsing exceptions.
 *  - For HTTP success (2xx):
 *      • We pull out the first choice’s content.
 *      • If no choices are returned, we use a clear, named fallback (NO_RESPONSE_FALLBACK).
 *  - For HTTP errors (non-2xx):
 *      • We build a human-readable string: "Error <code>: <errorBody>".
 *      • This makes it easy to diagnose server issues in tests or logs.
 */
class ChatRepository(
    private val api: FoundryApiService
) {
    suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.sendMessage(FoundryRequest(messages, maxTokens))
            if (response.isSuccessful) {
                val body = response.body()
                // Log raw JSON for debugging while hooking up the API
                Log.d("ChatRepository", "Response JSON: ${Gson().toJson(body)}")
                val first = body?.choices?.firstOrNull()?.message?.content
                first ?: NO_RESPONSE_FALLBACK
            } else {
                // pull the status code and raw error body
                val code = response.code()
                val errorText = response.errorBody()?.string().orEmpty()
                "Error $code: $errorText"
            }
        } catch (e: Exception) {
            // network failures, JSON parse errors, etc
            "Error: ${e.message}"
        }
    }

    private companion object {
        // Clear, constant message when API returns 200 but no choices
        const val NO_RESPONSE_FALLBACK = "No response from Foundry"
    }
}

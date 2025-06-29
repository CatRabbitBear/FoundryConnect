package com.booji.foundryconnect.data.network

import com.google.gson.annotations.SerializedName

/** Data class representing a single chat message for the Azure Foundry API. */
data class Message(
    val role: String,
    val content: String
)

/** Request body for the Azure Foundry chat completions endpoint. */
data class FoundryRequest(
    val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 256,
    val temperature: Double = 0.7
)

/** Container for the chat completions API response. */
data class FoundryResponse(
    val choices: List<Choice>
)

/** Wrapper for each choice returned from the API. */
data class Choice(
    val index: Int,
    val message: Message
)

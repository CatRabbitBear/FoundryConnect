package com.booji.foundryconnect.data.network

import com.azure.ai.openai.OpenAIClient
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.ChatCompletionsOptions
import com.azure.ai.openai.models.ChatRequestAssistantMessage
import com.azure.ai.openai.models.ChatRequestMessage
import com.azure.ai.openai.models.ChatRequestSystemMessage
import com.azure.ai.openai.models.ChatRequestUserMessage
import com.azure.core.credential.AzureKeyCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Chat backend using the Azure OpenAI SDK directly.
 */
class AzureOpenAiService(
    projectId: String,
    private val deployment: String,
    apiKey: String
) : ChatBackend {

    private val client: OpenAIClient

    init {
        val endpoint = "https://$projectId.cognitiveservices.azure.com/"
        client = OpenAIClientBuilder()
            .credential(AzureKeyCredential(apiKey))
            .endpoint(endpoint)
            .buildClient()
    }

    override suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String =
        withContext(Dispatchers.IO) {
            try {
                val reqMessages = messages.map { msg ->
                    when (msg.role) {
                        "assistant" -> ChatRequestAssistantMessage(msg.content)
                        "system" -> ChatRequestSystemMessage(msg.content)
                        else -> ChatRequestUserMessage(msg.content)
                    }
                }
                val options = ChatCompletionsOptions(reqMessages).apply {
                    setMaxTokens(maxTokens)
                    setTemperature(1.0)
                    setTopP(1.0)
                }
                val result = client.getChatCompletions(deployment, options)
                val first = result.choices.firstOrNull()?.message?.content
                first ?: "No response from Foundry"
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
}

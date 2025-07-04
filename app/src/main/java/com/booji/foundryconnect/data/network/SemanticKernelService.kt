package com.booji.foundryconnect.data.network

import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.microsoft.semantickernel.Kernel
import com.microsoft.semantickernel.orchestration.InvocationContext
import com.microsoft.semantickernel.orchestration.ToolCallBehavior
import com.microsoft.semantickernel.plugin.KernelPluginFactory
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory
import com.microsoft.semantickernel.services.chatcompletion.message.ChatMessageTextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Chat backend backed by Semantic Kernel and Azure OpenAI.
 */
class SemanticKernelService(
    projectId: String,
    private val model: String,
    apiKey: String,
    private val searchKey: String,
    firecrawlKey: String
) : ChatBackend {

    private val kernel: Kernel
    private val chat: ChatCompletionService

    init {
        val endpoint = "https://$projectId.cognitiveservices.azure.com/"
        val client = OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(AzureKeyCredential(apiKey))
            .buildAsyncClient()

        // Use the OpenAI chat completion service configured for Azure endpoint
        val completion = com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion
            .builder()
            .withModelId(model)
            .withOpenAIAsyncClient(client)
            .build()

        kernel = Kernel.builder()
            .withAIService(ChatCompletionService::class.java, completion)
            .withPlugin(KernelPluginFactory.createFromObject(WebSearchPlugin(searchKey, firecrawlKey), "web"))
            .build()

        chat = kernel.getService(ChatCompletionService::class.java)
    }

    override suspend fun sendMessage(messages: List<Message>, maxTokens: Int): String =
        withContext(Dispatchers.IO) {
            try {
                val history = ChatHistory()
                messages.forEach { msg ->
                    val content = when (msg.role) {
                        "assistant" -> ChatMessageTextContent.assistantMessage(msg.content)
                        "system" -> ChatMessageTextContent.systemMessage(msg.content)
                        else -> ChatMessageTextContent.userMessage(msg.content)
                    }
                    history.addMessage(content)
                }

                // Instead of passing `null` here…
                val invocationContext = InvocationContext.builder()
                    // include all kernel functions and let the model auto‐invoke them
                    .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                    .build()

                val result = chat.getChatMessageContentsAsync(history, kernel, invocationContext).block()
                // DEBUG: print out what kind of messages came back
                result?.forEach { c ->
                    println("→ Response chunk: [${c::class.simpleName}] $c")
                }
                val first = result?.lastOrNull() // as? ChatMessageTextContent
                first?.content ?: "No response from Foundry"
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
}

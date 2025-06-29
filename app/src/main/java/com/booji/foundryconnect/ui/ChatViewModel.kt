package com.booji.foundryconnect.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booji.foundryconnect.BuildConfig
import com.booji.foundryconnect.data.network.FoundryApiService
import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.data.repository.ChatRepository
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Simple ViewModel holding chat state for the UI layer.
 *
 * Currently this only stores messages typed by the user and responses
 * that will later come from the repository. Networking will be wired
 * in a future sprint.
 */
class ChatViewModel(
    private val repository: ChatRepository = defaultRepository()
) : ViewModel() {

    /** List of chat messages shown in the UI. */
    val messages = mutableStateListOf<Message>()

    /** Current text in the input field. */
    var inputText by mutableStateOf("")

    /** Flag indicating an in-flight network request. */
    var isLoading by mutableStateOf(false)

    /** Holds the latest error message, if any. */
    var errorMessage by mutableStateOf<String?>(null)

    /**
     * Sends a user message to the repository and updates UI state based on the
     * response. Any error text returned from the repository is surfaced via
     * [errorMessage] instead of being added to the chat transcript.
     */
    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        messages += Message(role = "user", content = prompt)
        inputText = ""

        viewModelScope.launch {
            isLoading = true
            val reply = repository.sendMessage(prompt)
            isLoading = false

            if (reply.startsWith("Error")) {
                errorMessage = reply
            } else {
                messages += Message(role = "assistant", content = reply)
                errorMessage = null
            }
        }
    }

    companion object {
        /** Builds a default [ChatRepository] using Retrofit and OkHttp. */
        private fun defaultRepository(): ChatRepository {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${BuildConfig.AZURE_API_KEY}")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val base = "https://${BuildConfig.AZURE_PROJECT}.cognitiveservices.azure.com/" +
                    "openai/deployments/${BuildConfig.AZURE_MODEL}/"

            val retrofit = Retrofit.Builder()
                .baseUrl(base)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(FoundryApiService::class.java)
            return ChatRepository(service)
        }
    }
}

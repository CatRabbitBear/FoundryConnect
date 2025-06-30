package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.FoundryRequest
import com.booji.foundryconnect.data.network.FoundryResponse
import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.data.network.Choice
import com.booji.foundryconnect.data.network.FoundryApiService
import okhttp3.mockwebserver.SocketPolicy
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.*

class ChatRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var repo: ChatRepository

    @Before
    fun setUp() {
        // Let OkHttp tests run without Android Log
        System.setProperty("okhttp.platform", "jdk9")

        server = MockWebServer().apply { start() }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        repo = ChatRepository(retrofit.create(FoundryApiService::class.java))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun sendMessage_success_parsesFirstChoiceContent() = runBlocking {
        // Given: a valid FoundryResponse JSON with one choice
        val json = """
          {
            "choices": [
              {
                "index": 0,
                "message": {
                  "role": "assistant",
                  "content": "Hello, Ant!"
                }
              }
            ]
          }
        """.trimIndent()
        server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        // When
        val reply = repo.sendMessage(listOf(Message("user", "Hi there")), 256)

        // Then
        assertEquals("Hello, Ant!", reply)
    }

    @Test
    fun sendMessage_multipleChoices_picksFirst() = runBlocking {
        // Given: multiple choices—ensure it picks the first
        val json = """
          {
            "choices": [
              {
                "index": 0,
                "message": { "role": "assistant", "content": "First reply" }
              },
              {
                "index": 1,
                "message": { "role": "assistant", "content": "Second reply" }
              }
            ]
          }
        """.trimIndent()
        server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        // When
        val reply = repo.sendMessage(listOf(Message("user", "Give me two")), 256)

        // Then
        assertEquals("First reply", reply)
    }

    @Test
    fun sendMessage_errorStatus_returnsErrorFallback() = runBlocking {
        // Given: HTTP 500 with some error text
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal error"))

        // When
        val reply = repo.sendMessage(listOf(Message("user", "Kaboom")), 256)

        // Then
        assertTrue(reply.contains("500"))
        assertTrue(reply.contains("Internal error"))
    }

    @Test
    fun sendMessage_emptyChoices_returnsFallback() = runBlocking {
        // Given: API returns 200 but with no choices
        val json = "{ \"choices\": [] }"
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        // When
        val reply = repo.sendMessage(listOf(Message("user", "No answer")), 256)

        // Then
        assertEquals("No response from Foundry", reply)
    }

    @Test
    fun sendMessage_networkException_returnsErrorMessage() = runBlocking {
        // Given: Socket disconnect to trigger an IOException
        server.enqueue(
            MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        )

        // When
        val reply = repo.sendMessage(listOf(Message("user", "Fail")), 256)

        // Then
        assertTrue(reply.startsWith("Error"))
    }
}

package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.FoundryRequest
import com.booji.foundryconnect.data.network.FoundryResponse
import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.data.network.Choice
import com.booji.foundryconnect.data.network.FoundryApiService
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
        val reply = repo.sendMessage("Hi there")

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
        val reply = repo.sendMessage("Give me two")

        // Then
        assertEquals("First reply", reply)
    }

    @Test
    fun sendMessage_errorStatus_returnsErrorFallback() = runBlocking {
        // Given: HTTP 500 with some error text
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal error"))

        // When
        val reply = repo.sendMessage("Kaboom")

        // Then
        assertTrue(reply.contains("500"))
        assertTrue(reply.contains("Internal error"))
    }
}

package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.FoundryApiService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class ChatRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: ChatRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(FoundryApiService::class.java)
        repository = ChatRepository(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun sendMessage_success() = runBlocking {
        val body = """
            {"choices":[{"index":0,"message":{"role":"assistant","content":"Hi"}}]}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        val result = repository.sendMessage("Hello")
        assertEquals("Hi", result)
    }

    @Test
    fun sendMessage_error() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))

        val result = repository.sendMessage("Hello")
        assertTrue(result.startsWith("Error"))
    }
}

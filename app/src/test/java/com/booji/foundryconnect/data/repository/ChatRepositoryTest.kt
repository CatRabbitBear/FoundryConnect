package com.booji.foundryconnect.data.repository

import com.booji.foundryconnect.data.network.Message
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChatRepositoryTest {

    private lateinit var repo: ChatRepository

    @Before
    fun setUp() {
        repo = ChatRepository(FakeChatBackend("ok"))
    }

    @Test
    fun sendMessage_success_parsesFirstChoiceContent() = runBlocking {
        repo = ChatRepository(FakeChatBackend("Hello, Ant!"))

        val reply = repo.sendMessage(listOf(Message("user", "Hi there")), 256)

        assertEquals("Hello, Ant!", reply)
    }

    @Test
    fun sendMessage_multipleChoices_picksFirst() = runBlocking {
        repo = ChatRepository(FakeChatBackend("First reply"))

        val reply = repo.sendMessage(listOf(Message("user", "Give me two")), 256)

        assertEquals("First reply", reply)
    }

    @Test
    fun sendMessage_errorStatus_returnsErrorFallback() = runBlocking {
        repo = ChatRepository(FakeChatBackend("Error 500: Internal error"))

        val reply = repo.sendMessage(listOf(Message("user", "Kaboom")), 256)

        assertTrue(reply.contains("500"))
        assertTrue(reply.contains("Internal error"))
    }

    @Test
    fun sendMessage_emptyChoices_returnsFallback() = runBlocking {
        repo = ChatRepository(FakeChatBackend("No response from Foundry"))

        val reply = repo.sendMessage(listOf(Message("user", "No answer")), 256)

        assertEquals("No response from Foundry", reply)
    }

    @Test
    fun sendMessage_networkException_returnsErrorMessage() = runBlocking {
        repo = ChatRepository(FakeChatBackend("Error: boom"))

        val reply = repo.sendMessage(listOf(Message("user", "Fail")), 256)

        assertTrue(reply.startsWith("Error"))
    }
}

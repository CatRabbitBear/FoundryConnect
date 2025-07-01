package com.booji.foundryconnect.ui

import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.data.repository.ChatRepository
import com.booji.foundryconnect.data.repository.FakeChatBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class ChatViewModelTest {

    @Before
    fun setup() {
        // Main dispatcher will be provided per-test using the runTest scheduler
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sendMessage_addsAssistantMessageOnSuccess() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ChatRepository(FakeChatBackend("Hi"))
        val vm = ChatViewModel(repo)

        vm.sendMessage("Hello")
        advanceUntilIdle()

        assertEquals(2, vm.messages.size)
        assertEquals("Hi", vm.messages.last().content)
        assertNull(vm.errorMessage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sendMessage_setsErrorMessageOnFailure() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ChatRepository(FakeChatBackend("Error: bad"))
        val vm = ChatViewModel(repo)

        vm.sendMessage("Hello")
        advanceUntilIdle()

        assertEquals(1, vm.messages.size)
        assertEquals("Error: bad", vm.errorMessage)
    }
}

package com.booji.foundryconnect.data.history

import com.booji.foundryconnect.data.network.Message
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryReducerTest {
    @Test
    fun buildContext_trimsOldMessagesAndAddsPlaceholder() {
        val history = listOf(
            Message("user", "First message"),
            Message("assistant", "Reply one"),
            Message("user", "Second message")
        )
        val result = HistoryReducer.buildContext(history, "sys", 3)
        // Should keep only the last message plus placeholder and system
        assertEquals(3, result.size)
        assertEquals("system", result[0].role)
        assertEquals("Some old messages removed from context...", result[1].content)
        assertEquals("Second message", result[2].content)
    }
}

package com.booji.foundryconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.booji.foundryconnect.data.history.ChatHistoryStore
import com.booji.foundryconnect.data.history.ChatRecord
import kotlinx.coroutines.launch

/**
 * Simple screen listing previous chats with options to resume or delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    store: ChatHistoryStore,
    onStartNew: () -> Unit,
    onOpenChat: (ChatRecord) -> Unit
) {
    val chats by store.chats.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Previous Chats") }) }
    ) { inner ->
        Column(modifier = Modifier.padding(inner).fillMaxSize()) {
            Button(onClick = onStartNew, modifier = Modifier.padding(16.dp)) {
                Text("Start New Chat")
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chats.asReversed()) { chat ->
                    ChatRow(
                        chat = chat,
                        onOpen = { onOpenChat(chat) },
                        onDelete = { scope.launch { store.deleteChat(chat.id) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatRecord, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val preview = chat.messages.firstOrNull()?.content?.take(40) ?: "(empty)"
        Button(onClick = onOpen) { Text(preview) }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onDelete) { Text("Delete") }
    }
}

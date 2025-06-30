package com.booji.foundryconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.booji.foundryconnect.data.network.Message
import com.booji.foundryconnect.ui.ChatViewModel
import com.booji.foundryconnect.ui.components.MessageBubble

/**
 * Main UI screen showing chat interactions between user and Azure Foundry model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = remember { ChatViewModel() },
    onOpenSettings: () -> Unit = {}
) {
    val error = viewModel.errorMessage
    val loading = viewModel.isLoading

    val messages = viewModel.messages
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foundry Chat") },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("Settings") }
                }
            )
        },
        bottomBar = {
            MessageInput(
                text = viewModel.inputText,
                onTextChange = { viewModel.inputText = it },
                onSend = { viewModel.sendMessage(it) },
                enabled = !loading
            )
        }
    ) { inner ->
        Box(modifier = Modifier.padding(inner).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                state = listState,
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    MessageBubble(message = msg.content, isUser = msg.role == "user")
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            error?.let { err ->
                Text(
                    text = err,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: (String) -> Unit,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            placeholder = { Text("Type a message") }
        )
        Spacer(Modifier.width(8.dp))
        Button(onClick = { onSend(text) }, enabled = enabled) {
            Text("Send")
        }
    }
}

// -------- Previews ---------

@Preview(showBackground = true)
@Composable
private fun EmptyChatPreview() {
    val vm = remember { ChatViewModel() }
    ChatScreen(viewModel = vm)
}

@Preview(showBackground = true)
@Composable
private fun FilledChatPreview() {
    val vm = remember {
        ChatViewModel().apply {
            messages += Message("user", "Hello")
            messages += Message("assistant", "Hi there!")
        }
    }
    ChatScreen(viewModel = vm)
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    val vm = remember {
        ChatViewModel().apply {
            isLoading = true
        }
    }
    ChatScreen(viewModel = vm)
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    val vm = remember {
        ChatViewModel().apply {
            errorMessage = "Something went wrong"
        }
    }
    ChatScreen(viewModel = vm)
}

package com.booji.foundryconnect

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.booji.foundryconnect.data.history.ChatHistoryStore
import com.booji.foundryconnect.data.history.ChatRecord
import com.booji.foundryconnect.data.prefs.SettingsDataStore
import com.booji.foundryconnect.ui.ChatViewModel
import com.booji.foundryconnect.ui.createRepository
import com.booji.foundryconnect.ui.screens.ChatListScreen
import com.booji.foundryconnect.ui.screens.ChatScreen
import com.booji.foundryconnect.ui.screens.SettingsScreen

/**
 * Root composable handling simple in-app navigation and repository setup.
 */
@Composable
fun FoundryChatApp() {
    val context = LocalContext.current
    val store = remember { SettingsDataStore(context) }
    val chatStore = remember { ChatHistoryStore(context) }

    // Observe stored values with BuildConfig fallbacks
    val project by store.projectId.collectAsState(initial = BuildConfig.AZURE_PROJECT)
    val model by store.modelName.collectAsState(initial = BuildConfig.AZURE_MODEL)
    val key by store.apiKey.collectAsState(initial = BuildConfig.AZURE_API_KEY)

    // Rebuild repository when settings change
    val repository = remember(project, model, key) {
        createRepository(
            project.ifBlank { BuildConfig.AZURE_PROJECT },
            model.ifBlank { BuildConfig.AZURE_MODEL },
            key.ifBlank { BuildConfig.AZURE_API_KEY }
        )
    }
    val viewModel = remember(repository, chatStore, store) { ChatViewModel(repository, chatStore, store) }

    val chats: List<ChatRecord>? by chatStore.chats.collectAsState(initial = null)
    var screen by remember { mutableStateOf<Screen?>(null) }
    LaunchedEffect(chats) {
        if (screen == null && chats != null) {
            screen = if (chats!!.isEmpty()) Screen.Chat else Screen.List
        }
    }

    val current = screen
    when (current) {
        Screen.Chat -> ChatScreen(
            viewModel,
            onOpenSettings = { screen = Screen.Settings },
            onOpenChats = {
                viewModel.persistChatIfNeeded()
                screen = Screen.List
            }
        )
        Screen.Settings -> SettingsScreen(store) { screen = Screen.Chat }
        Screen.List -> ChatListScreen(
            store = chatStore,
            onStartNew = {
                viewModel.startNewChat()
                screen = Screen.Chat
            },
            onOpenChat = {
                viewModel.loadChat(it)
                screen = Screen.Chat
            }
        )
        null -> {}
    }
}

private enum class Screen { Chat, Settings, List }

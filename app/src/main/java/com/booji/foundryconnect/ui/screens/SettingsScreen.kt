package com.booji.foundryconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.booji.foundryconnect.data.prefs.SettingsDataStore
import kotlinx.coroutines.launch

/** Screen allowing entry of Azure credentials. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(store: SettingsDataStore, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    val currentProject by store.projectId.collectAsState(initial = "")
    val currentModel by store.modelName.collectAsState(initial = "")
    val currentKey by store.apiKey.collectAsState(initial = "")
    val currentTokens by store.maxTokens.collectAsState(initial = 256)
    val currentHistory by store.historyWords.collectAsState(initial = 1000)
    val currentSystem by store.systemMessage.collectAsState(initial = "")
    val currentService by store.serviceId.collectAsState(initial = "")

    var project by remember { mutableStateOf(currentProject) }
    var model by remember { mutableStateOf(currentModel) }
    var key by remember { mutableStateOf(currentKey) }
    var tokens by remember { mutableStateOf(currentTokens.toString()) }
    var history by remember { mutableStateOf(currentHistory.toString()) }
    var system by remember { mutableStateOf(currentSystem) }
    var service by remember { mutableStateOf(currentService) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { inner ->
        Column(modifier = Modifier.padding(inner).padding(16.dp)) {
            OutlinedTextField(
                value = project,
                onValueChange = { project = it },
                label = { Text("Project ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = tokens,
                onValueChange = { tokens = it },
                label = { Text("Max Tokens") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = history,
                onValueChange = { history = it },
                label = { Text("History Words") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = system,
                onValueChange = { system = it },
                label = { Text("System Prompt") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = service,
                onValueChange = { service = it },
                label = { Text("Service ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    // Parse ints, falling back if the user left it blank or non-numeric
                    val t = tokens.toIntOrNull() ?: currentTokens
                    val h = history.toIntOrNull() ?: currentHistory

                    // Use the new text if non-blank, otherwise keep the previous value
                    val newProject = project.ifBlank { currentProject }
                    val newModel   = model.ifBlank   { currentModel   }
                    val newKey     = key.ifBlank     { currentKey     }
                    val newSystem  = system.ifBlank  { currentSystem  }
                    val newService = service.ifBlank { currentService }

                    store.save(
                        newProject,
                        newModel,
                        newKey,
                        t,
                        h,
                        newSystem,
                        newService
                    )

                    onBack()
                }
            }) {
                Text("Save")
            }
        }
    }
}

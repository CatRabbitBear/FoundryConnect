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

    var project by remember { mutableStateOf(currentProject) }
    var model by remember { mutableStateOf(currentModel) }
    var key by remember { mutableStateOf(currentKey) }

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
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                scope.launch {
                    store.save(project, model, key)
                    onBack()
                }
            }) {
                Text("Save")
            }
        }
    }
}

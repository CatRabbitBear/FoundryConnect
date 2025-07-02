# FoundryConnect

FoundryConnect is a minimal Android chat app that talks to **Azure Foundry**. It stores your Azure credentials locally via Android's DataStore and sends chat prompts to the Foundry REST API.

## Configuration

Create an environment file at `app/assets/env` before building the project. The Gradle script reads this file and exposes each value as a `BuildConfig` field:

```env
AZURE_PROJECT=<your-project-id>
AZURE_MODEL=<your-model-name>
AZURE_API_KEY=<your-api-key>
SERP_API_KEY=<your-serpapi-search-key>
FIRECRAWL_API_KEY=<your-firecrawl-api-key>
```

These values populate the BuildConfig fields defined in `app/build.gradle.kts`:

```kotlin
val envFile = rootProject.file("app/assets/env")
val envMap: Map<String, String> = if (envFile.exists()) {
    envFile.readLines()
        .mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                null
            } else {
                val (key, value) = trimmed.split("=", limit = 2)
                key.trim() to value.trim().trim('"', '\'')
            }
        }
        .toMap()
} else {
    emptyMap()
}
```

The `AZURE_PROJECT`, `AZURE_MODEL`, `AZURE_API_KEY`, and `BING_API_KEY` fields are later retrieved using `BuildConfig`.

## DataStore settings

`SettingsDataStore` saves the credentials so the app can restore them between runs. The class exposes three `Flow` objects and a `save` method:

```kotlin
val projectId: Flow<String> = context.settingsDataStore.data.map { it[KEY_PROJECT] ?: "" }
val modelName: Flow<String> = context.settingsDataStore.data.map { it[KEY_MODEL] ?: "" }
val apiKey: Flow<String> = context.settingsDataStore.data.map { it[KEY_KEY] ?: "" }

suspend fun save(project: String, model: String, key: String) {
    context.settingsDataStore.edit { prefs ->
        prefs[KEY_PROJECT] = project
        prefs[KEY_MODEL] = model
        prefs[KEY_KEY] = key
    }
}
```

`FoundryChatApp` observes these flows and re‑creates the repository whenever a value changes so network calls always use the latest configuration.

## Chat flow

1. `ChatScreen` displays messages and collects user input.
2. When the user taps **Send**, `ChatViewModel.sendMessage()` appends their message and calls `ChatRepository`:

```kotlin
viewModelScope.launch {
    isLoading = true
    val reply = repository.sendMessage(conversation)
    isLoading = false

    if (reply.startsWith("Error")) {
        errorMessage = reply
    } else {
        messages += Message(role = "assistant", content = reply)
        errorMessage = null
    }
}
```

3. `ChatRepository.sendMessage()` posts the conversation to the Azure Foundry endpoint and returns either the first response choice or an error string:

```kotlin
val response = api.sendMessage(FoundryRequest(messages))
if (response.isSuccessful) {
    val body = response.body()
    val first = body?.choices?.firstOrNull()?.message?.content
    first ?: "No response from Foundry"
} else {
    val code = response.code()
    val errorText = response.errorBody()?.string().orEmpty()
    "Error $code: $errorText"
}
```

4. Successful replies are added to the message list. Errors are shown at the top of the screen.

## Running tests

Use Gradle to execute the unit tests:

```bash
./gradlew test
```

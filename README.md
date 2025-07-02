# FoundryConnect

FoundryConnect is a small prototype Android chat client for **Azure Foundry**. It was built as a learning exercise and is not meant for day‑to‑day use. The code is unlikely to be maintained going forward.

## Setup

Create a folder named `assets` inside the `app` module and then create a file called `env` within it (do **not** prefix the file name with a dot). Android Studio may only show this directory in **Project** view.

Populate `app/assets/env` with your credentials:

```env
AZURE_PROJECT=<your-foundry-project-id>
AZURE_MODEL=<your-model-name>
AZURE_API_KEY=<your-api-key>
SERP_API_KEY=<your-serpapi-key>
FIRECRAWL_API_KEY=<your-firecrawl-key>
```

The app requires an Azure Foundry endpoint, model and API key to send prompts. Web search happens in two steps: a SERPAPI query returns search results and Firecrawl then fetches markdown for a subset of those URLs. Both services offer free tiers.

Gradle reads this file and exposes each entry as a `BuildConfig` field so the app can access the values at runtime.

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

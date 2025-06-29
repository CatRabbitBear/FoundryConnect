# AGENTS.md - Codex Instructions for FoundryConnect App

## Project Goals:

* Deliver a minimal, functional MVP Android chat app.
* Clearly demonstrate integration with Azure Foundry's REST API.
* Provide clean separation between UI (Jetpack Compose), business logic (ChatRepository), and data/network layers (FoundryApiService).

## Expected Components and Behaviors:

### API Layer

* **Retrofit interface (`FoundryApiService`)**:

  * Use `Response<FoundryResponse>` return type to inspect both success and error HTTP codes.
  * Sends a POST request with `FoundryRequest(messages: List<Message>)` and parses JSON into `FoundryResponse`.

**Sample Request (bash):**
Where the environment variables include AZURE_PROJECT, AZURE_MODEL, AZURE_API_KEY
```bash
curl -X POST "https://{$AZURE_POJECT}.cognitiveservices.azure.com/openai/deployments/{$AZURE_MODEL}/chat/completions?api-version=2025-01-01-preview" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AZURE_API_KEY" \
  -d '{
      "messages": [{"role":"user","content":"Hello!"}],
      "max_tokens":4096,
      "temperature":1,
      "top_p":1,
      "model":"{$AZURE_MODEL}"
    }'
```

### Repository Layer (`ChatRepository`)

* Handle messaging logic with Coroutines (`Dispatchers.IO`).
* On **successful** HTTP (2xx):

  * Extract first choice via `response.body()?.choices?.firstOrNull()?.message?.content`.
  * If no choices, return a clear, constant fallback (`"No response from Foundry"`).
* On **error** HTTP (non-2xx):

  * Read `response.code()` and `response.errorBody()?.string()`.
  * Return uniform error string: `"Error <code>: <errorText>"`.
* Wrap entire call in `try/catch` to catch network or parse exceptions, returning `"Error: ${e.message}"`.

### UI Layer (Jetpack Compose)

* **`ChatScreen.kt`**:

  * Display a vertically scrollable list of messages.
  * Provide an input field and send button at the bottom.
  * Show a loading indicator while awaiting API responses.
  * Display error text from the repository if any.

* **`MessageBubble.kt`**:

  * Display messages differently for user vs. AI.
  * Optional timestamp or metadata parameter.

### Testing Requirements

* Use **MockWebServer** for unit tests against `ChatRepository`:

  1. **Happy path:** 200 OK with valid `choices` JSON → assert returned content.
  2. **Multiple choices:** ensure first is picked.
  3. **Empty choices:** assert fallback `"No response from Foundry"`.
  4. **HTTP error:** 500 with body → assert `"Error 500: <body>"`.
  5. **Exception path:** mock network or parse exception → assert `"Error: ..."`.

### Coding Guidelines for Codex:

* Maintain **readability** and **clean architecture**.
* Demonstrate **explicit** error handling paths.
* Follow **idiomatic Kotlin**, use Coroutines for async, and Jetpack Compose best practices.
* Provide **inline documentation** summarizing complex logic or non-obvious decisions.
* Ensure tests are **deterministic**, using MockWebServer and clear assertions.

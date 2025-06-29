# AGENTS.md - Codex Instructions for FoundryChat App

## Project Goals:
- Deliver a minimal, functional MVP Android chat app.
- Clearly demonstrate integration with Azure Foundry's REST API.
- Provide clean separation between UI (Jetpack Compose), business logic, and data/network layers.

## Expected Components and Behaviors:

### API Layer
- Retrofit interface (`FoundryApiService`):
    - Sends prompt to Azure Foundry via POST request.
    - Receives and parses response into clear, concise data classes.

### Repository Layer
- Handles all business logic around messaging.
- Clearly implements error handling, async network requests (Coroutines), and clean response parsing.

### UI Layer (Jetpack Compose)
- Main chat screen (`ChatScreen.kt`):
    - Vertical scrollable message display.~~~~~~~~
    - Message input and send button at bottom.
    - Loading indicators during network calls.
    - Error messages gracefully communicated to the user.

- Message bubble component (`MessageBubble.kt`):
    - Styled differently based on sender (user or AI).
    - Optionally includes timestamp or other metadata.

### Coding Guidelines for Codex:
- Maintain readability, clean architecture, and separation of concerns.
- Prioritize robust error handling and clear, understandable logic.
- Follow idiomatic Kotlin and Jetpack Compose best practices.
- Clearly document methods and classes, and leave informative inline comments.
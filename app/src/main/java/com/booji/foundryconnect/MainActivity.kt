package com.booji.foundryconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.booji.foundryconnect.ui.theme.FoundryConnectTheme


/**
 * Main entry point for the FoundryChat app.
 *
 * TODO(Codex): Verify basic Compose setup; no major logic here, just app initialization.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoundryConnectTheme {
                FoundryChatApp()
            }
        }
    }
}
package com.booji.foundryconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.booji.foundryconnect.ui.theme.FoundryConnectTheme
import com.booji.foundryconnect.ui.screens.ChatScreen


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
                ChatScreen()
            }
        }
    }
}
package com.booji.foundryconnect.ui.util

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Displays markdown content using a [TextView] and [MarkdownFormatter].
 */
@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Ensure Markwon instance exists
    MarkdownFormatter.init(context)
    val parsed = remember(text) { MarkdownFormatter.parse(text) }
    AndroidView(
        factory = { TextView(context) },
        modifier = modifier
    ) { view ->
        view.text = parsed
    }
}

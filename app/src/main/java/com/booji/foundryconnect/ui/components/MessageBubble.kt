package com.booji.foundryconnect.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Simple message bubble used in the chat list.
 * Styling is intentionally basic for now – real visuals can iterate later.
 */
@Composable
fun MessageBubble(message: String, isUser: Boolean) {
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else Color.LightGray
    val contentColor = if (isUser) Color.White else Color.Black

    Card(
        colors = CardDefaults.cardColors(containerColor = bubbleColor, contentColor = contentColor),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(if (isUser) 8.dp else 4.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.CenterStart) {
            Text(text = message)
        }
    }
}

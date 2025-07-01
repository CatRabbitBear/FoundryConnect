package com.booji.foundryconnect.ui.util

import android.content.Context
import android.text.Spanned
import io.noties.markwon.Markwon

/**
 * Simple wrapper around [Markwon] so the rest of the code doesn't
 * need to know about the library. Call [init] once with an Android
 * [Context] then use [parse] to convert markdown strings to [Spanned].
 */
object MarkdownFormatter {
    private var markwon: Markwon? = null

    /**
     * Initialise the underlying [Markwon] instance if required.
     */
    fun init(context: Context) {
        if (markwon == null) {
            markwon = Markwon.create(context)
        }
    }

    /**
     * Convert the given markdown text into a [Spanned] instance.
     * [init] must be called before using this function.
     */
    fun parse(markdown: String): Spanned {
        val mk = markwon
            ?: throw IllegalStateException("MarkdownFormatter not initialised")
        return mk.toMarkdown(markdown)
    }
}

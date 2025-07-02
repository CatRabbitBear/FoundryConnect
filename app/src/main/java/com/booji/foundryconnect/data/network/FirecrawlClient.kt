package com.booji.foundryconnect.data.network

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Utility for talking to the Firecrawl scraping API.
 *
 * This is NOT exposed directly to the model.  Instead your
 * top-level kernel function will call this under the hood.
 */
class FirecrawlClient(
    private val client: OkHttpClient,
    private val apiKey: String
) {
    private val JSON: MediaType = "application/json".toMediaType()

    /**
     * Scrape the given URL and return its markdown via Firecrawl.
     *
     * @throws RuntimeException on HTTP or API failure.
     */
    fun fetchMarkdown(url: String): String {
        // Build the POST body
        val bodyJson = JSONObject().apply {
            put("url", url)
            put("formats", listOf("markdown"))
        }.toString()

        val request = Request.Builder()
            .url("https://api.firecrawl.dev/v1/scrape")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(bodyJson.toRequestBody(JSON))
            .build()

        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                throw RuntimeException("Firecrawl HTTP error: ${resp.code}")
            }
            val text = resp.body.string().orEmpty()
            val obj  = JSONObject(text)

            if (!obj.optBoolean("success", false)) {
                throw RuntimeException("Firecrawl reported failure")
            }

            val data     = obj.getJSONObject("data")
            val markdown = data.optString("markdown", "")
            return markdown.ifBlank { "No markdown content returned." }
        }
    }
}
package com.booji.foundryconnect.data.network

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Simple plugin exposing a Bing Web Search function to Semantic Kernel.
 */
class WebSearchPlugin(private val apiKey: String) {

    private val client = OkHttpClient()

    @DefineKernelFunction(
        name = "web_search",
        description = "Search the web and return a short snippet from the top result",
        returnType = "String",
        returnDescription = "Snippet text"
    )
    fun search(
        @KernelFunctionParameter(name = "query", description = "Search query")
        query: String
    ): String {
        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://api.bing.microsoft.com/v7.0/search?q=$encoded&count=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("Ocp-Apim-Subscription-Key", apiKey)
                .build()
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return "Error ${'$'}{resp.code}"
                }
                val json = JSONObject(resp.body?.string().orEmpty())
                val snippet = json
                    .optJSONObject("webPages")
                    ?.optJSONArray("value")
                    ?.optJSONObject(0)
                    ?.optString("snippet")
                snippet ?: "No results"
            }
        } catch (e: Exception) {
            "Error: ${'$'}{e.message}"
        }
    }
}

package com.booji.foundryconnect.data.network

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WebSearchPlugin(private val apiKey: String, private val firecrawlApiKey: String) {
    private val client = OkHttpClient()
    private val firecrawl = FirecrawlClient(client, firecrawlApiKey)
    private val topNResults = 2

    @DefineKernelFunction(
        name = "search_web",
        description = "Run a web search via SerpApi and return summarized top results",
        returnType = "String",
        returnDescription = "Search summary"
    )
    fun search(@KernelFunctionParameter(name = "query") query: String): String {
        return try {
            // Build request to SerpApi
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("serpapi.com")
                .addPathSegment("search.json")
                .addQueryParameter("q", query)
                .addQueryParameter("api_key", apiKey)
                .build()

            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    throw RuntimeException("SerpApi failed: ${resp.code}")
                }

                val text = resp.body.string().orEmpty()
                val root = JSONObject(text)
                val organic = root.optJSONArray("organic_results") ?: return "No results found"

                val results = org.json.JSONArray()
                val limit = minOf(topNResults, organic.length())
                for (i in 0 until limit) {
                    val item = organic.getJSONObject(i)
                    val link = item.optString("link")
                    if (link.isNullOrBlank()) continue

                    val markdown = try {
                        firecrawl.fetchMarkdown(link)
                    } catch (e: Exception) {
                        "Error fetching markdown: ${e.message}"
                    }

                    val entry = JSONObject().apply {
                        put("position", item.optInt("position", i + 1))
                        put("title", item.optString("title"))
                        put("url", link)
                        put("snippet", item.optString("snippet"))
                        put("markdown", markdown)
                    }
                    results.put(entry)
                }

                return JSONObject().apply {
                    put("results", results)
                }.toString()
            }
        } catch (e: Exception) {
            // Return the error to the caller so the LLM can continue operating
            "Error: ${e.message}"
        }
    }
}

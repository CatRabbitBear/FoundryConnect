package com.booji.foundryconnect.data.network

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

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
    fun search(@KernelFunctionParameter(name="query") query: String): String {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("serpapi.com")
            .addPathSegment("search.json")
            .addQueryParameter("q", query)
            .addQueryParameter("api_key", apiKey)
            .build()

        val req = Request.Builder().url(url).build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw RuntimeException("SerpApi failed: ${resp.code}")
            val json = resp.body.string().orEmpty()
            // Retrieve the topNResults from "organic_results"
            // Iterate over the organic results and call firecrawl.fetchMarkdown on "link" in each organic result object
            // collate the results into a json string (ready to send back to LLM).
            // Note: Perhaps maintain the url in the return JSON objects so the downstream AI can
            // judge the source and pass this on to the user.
            return json
        }
    }
}

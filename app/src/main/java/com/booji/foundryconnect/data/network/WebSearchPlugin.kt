package com.booji.foundryconnect.data.network

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class SerpApiPlugin(private val apiKey: String) {
    private val client = OkHttpClient()

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
            // Optionally parse and summarize title/snippet of top 3 results
            return json
        }
    }
}

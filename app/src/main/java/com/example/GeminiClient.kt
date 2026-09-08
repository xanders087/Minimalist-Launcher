package com.example

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String? = null
)

data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

data class Candidate(
    val content: Content? = null
)

data class AppCategoryResult(
    val packageName: String? = null,
    val appName: String? = null,
    val category: String? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val STANDARD_CATEGORIES = listOf(
        "Communication",
        "Productivity",
        "Entertainment",
        "Finance",
        "Utilities",
        "Health & Fitness",
        "Navigation",
        "System"
    )

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Batch analyzes multiple installed apps and assigns each to a logical category
     * such as 'Communication', 'Productivity', 'Entertainment', 'Finance', 'Utilities', etc.
     */
    suspend fun categorizeAllApps(apps: List<AppInfo>): Map<String, String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apps.isEmpty()) {
            return@withContext emptyMap()
        }

        val appListDescription = apps.joinToString(separator = "\n") { app ->
            "- Label: \"${app.label}\", Package: \"${app.packageName}\""
        }

        val systemInstruction = """
            You are an AI categorization engine for an Android device.
            Analyze the provided list of installed apps and classify EACH app into exactly ONE of the following logical categories:
            - Communication (Messaging, Dialers, Email, Social networks, Video calls)
            - Productivity (Browsers, Office tools, Notes, Cloud storage, Calculators, Calendars, Task managers)
            - Entertainment (Games, Streaming video, Music, Podcasts, Photos, Media players)
            - Finance (Banking, Digital wallets, Crypto, Expense trackers, Payment apps)
            - Utilities (Clock, File managers, Weather, Settings, Hardware tools)
            - Health & Fitness (Workout trackers, Meditation, Health records)
            - Navigation (Maps, GPS, Transit, Ride sharing)
            - System (Core Android system components, App store, System package handlers)

            Return ONLY a valid JSON array of objects with the keys "packageName" and "category".
            Example:
            [
              {"packageName": "com.google.android.dialer", "category": "Communication"},
              {"packageName": "com.android.chrome", "category": "Productivity"},
              {"packageName": "com.spotify.music", "category": "Entertainment"}
            ]
        """.trimIndent()

        val prompt = "Categorize these installed apps:\n$appListDescription"

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        try {
            val response = service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: return@withContext emptyMap()

            // Clean up any markdown code block fences e.g. ```json ... ```
            val cleanedJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val startIndex = cleanedJson.indexOf('[')
            val endIndex = cleanedJson.lastIndexOf(']')
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                val jsonArrayStr = cleanedJson.substring(startIndex, endIndex + 1)
                val type = Types.newParameterizedType(List::class.java, AppCategoryResult::class.java)
                val adapter = moshi.adapter<List<AppCategoryResult>>(type)
                val results = adapter.fromJson(jsonArrayStr) ?: emptyList()

                val resultMap = mutableMapOf<String, String>()
                for (item in results) {
                    val pkg = item.packageName
                    val cat = item.category?.trim()
                    if (!pkg.isNullOrEmpty() && !cat.isNullOrEmpty()) {
                        val matchedCategory = matchCategory(cat)
                        resultMap[pkg] = matchedCategory
                    }
                }
                return@withContext resultMap
            }
            emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Categorizes a single app by name using Gemini API.
     */
    suspend fun categorizeApp(appName: String, packageName: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext matchCategory(appName)
        }
        val prompt = "Classify the Android app with label \"$appName\" (package: \"$packageName\") into a single logical category."
        val systemInstruction = """
            You are an AI categorization engine for an Android device.
            Classify the application into EXACTLY ONE of these standard categories:
            Communication, Productivity, Entertainment, Finance, Utilities, Health & Fitness, Navigation, System.
            Respond ONLY with the category name. No extra words or punctuation.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )
        try {
            val response = service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
            matchCategory(text)
        } catch (e: Exception) {
            "Utilities"
        }
    }

    private fun matchCategory(input: String): String {
        for (std in STANDARD_CATEGORIES) {
            if (std.equals(input, ignoreCase = true)) {
                return std
            }
        }
        // Check partial matches or Spanish equivalents
        val lower = input.lowercase()
        return when {
            lower.contains("commun") || lower.contains("comunic") || lower.contains("chat") || lower.contains("social") || lower.contains("mensaje") -> "Communication"
            lower.contains("product") || lower.contains("trabajo") || lower.contains("work") || lower.contains("office") || lower.contains("browser") || lower.contains("doc") -> "Productivity"
            lower.contains("entertain") || lower.contains("entreten") || lower.contains("game") || lower.contains("juego") || lower.contains("music") || lower.contains("video") || lower.contains("movie") || lower.contains("photo") -> "Entertainment"
            lower.contains("finan") || lower.contains("bank") || lower.contains("banco") || lower.contains("pay") || lower.contains("pago") || lower.contains("money") -> "Finance"
            lower.contains("health") || lower.contains("salud") || lower.contains("fit") || lower.contains("bienestar") || lower.contains("gym") -> "Health & Fitness"
            lower.contains("navig") || lower.contains("map") || lower.contains("movil") || lower.contains("gps") || lower.contains("transit") || lower.contains("travel") -> "Navigation"
            lower.contains("system") || lower.contains("sistema") || lower.contains("setting") || lower.contains("ajuste") -> "System"
            else -> "Utilities"
        }
    }
}

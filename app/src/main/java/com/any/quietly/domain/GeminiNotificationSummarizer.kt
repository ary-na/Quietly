package com.any.quietly.domain

import com.any.quietly.BuildConfig
import com.any.quietly.data.NotificationData
import com.google.ai.client.generativeai.GenerativeModel

class GeminiNotificationSummarizer {

    suspend fun summarizeNotifications(notifications: List<NotificationData>): String? {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return "API Key not set. Please add your Gemini API key to local.properties."
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        val prompt = buildString {
            append("Summarize the following notifications in a single, easy-to-read paragraph. Group similar notifications if possible:\n\n")
            notifications.forEach {
                append("- Title: ${it.title}, Content: ${it.text}\n")
            }
        }

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            // Handle exceptions, e.g., network errors, API errors
            "Error generating summary: ${e.message}"
        }
    }
}

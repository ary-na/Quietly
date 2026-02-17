package com.any.quietly.data

data class QuietWindow(
    val id: Int = 0,
    val name: String,
    val startTime: Long, // Start time of the quiet window.
    val endTime: Long, // Notification summary is delivered.
    val notifications: List<NotificationData> = emptyList()
)

package com.any.quietly.data

data class QuietWindow(
    val id: Int = 0,
    val name: String,
    val notificationCount: Int, // Number of notifications to trigger a summary.
    val isEnabled: Boolean = true
)

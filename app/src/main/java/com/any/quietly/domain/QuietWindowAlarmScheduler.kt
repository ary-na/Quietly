package com.any.quietly.domain

import android.content.Context
import android.util.Log
import com.any.quietly.data.QuietWindow

// Legacy scheduler kept only for compatibility while the app now uses count-based windows.
class QuietWindowAlarmScheduler(@Suppress("UNUSED_PARAMETER") private val context: Context) {
    fun schedule(quietWindow: QuietWindow) {
        Log.d("QuietWindowAlarm", "Scheduling disabled for count-based quiet window: ${quietWindow.id}")
    }

    fun cancel(quietWindow: QuietWindow) {
        Log.d("QuietWindowAlarm", "Cancel disabled for count-based quiet window: ${quietWindow.id}")
    }
}

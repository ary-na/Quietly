package com.any.quietly.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.any.quietly.repository.NotificationRepository
import com.any.quietly.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: NotificationRepository by inject()
    private val summarizer: GeminiNotificationSummarizer by inject()
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val quietWindowId = intent.getIntExtra("EXTRA_QUIET_WINDOW_ID", -1)
        if (quietWindowId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val quietWindow = repository.getQuietWindow(quietWindowId).firstOrNull() ?: return@launch

                if (quietWindow.notifications.isNotEmpty()) {
                    val summary = summarizer.summarizeNotifications(quietWindow.notifications)
                    if (summary != null) {
                        notificationHelper.showSummaryNotification(quietWindow.name, summary)
                    }
                }

                // Now clear the notifications for the next cycle
                repository.clearNotificationsForQuietWindow(quietWindowId)
            }
        }
    }
}

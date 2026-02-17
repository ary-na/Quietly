package com.any.quietly.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.any.quietly.data.QuietWindow

class QuietWindowAlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(quietWindow: QuietWindow) {
        if (alarmManager == null) {
            Log.e("QuietWindowAlarm", "AlarmManager unavailable; skipping schedule.")
            return
        }
        if (quietWindow.endTime <= System.currentTimeMillis()) {
            Log.w("QuietWindowAlarm", "End time is in the past; skipping schedule.")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_QUIET_WINDOW_ID", quietWindow.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            quietWindow.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        quietWindow.endTime,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm if exact alarms aren't allowed.
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        quietWindow.endTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    quietWindow.endTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("QuietWindowAlarm", "Exact alarm not permitted; scheduling inexact.", e)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                quietWindow.endTime,
                pendingIntent
            )
        }
    }

    fun cancel(quietWindow: QuietWindow) {
        if (alarmManager == null) {
            Log.e("QuietWindowAlarm", "AlarmManager unavailable; skipping cancel.")
            return
        }
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            quietWindow.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

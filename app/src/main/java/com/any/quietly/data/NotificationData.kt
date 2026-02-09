package com.any.quietly.data

import android.app.Notification
import android.os.Bundle
import android.service.notification.StatusBarNotification

data class NotificationData(
    val id: Int = 0,
    val packageName: String,
    val notificationId: Int,
    val tag: String?,
    val title: String?,
    val text: String?,
    val postTime: Long
) {
    companion object {
        fun fromStatusBarNotification(sbn: StatusBarNotification): NotificationData {
            val extras: Bundle = sbn.notification.extras
            return NotificationData(
                packageName = sbn.packageName,
                notificationId = sbn.id,
                tag = sbn.tag,
                title = extras.getString(Notification.EXTRA_TITLE),
                text = extras.getString(Notification.EXTRA_TEXT),
                postTime = sbn.postTime
            )
        }
    }
}

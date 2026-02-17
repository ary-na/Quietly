package com.any.quietly.data

import androidx.room.Embedded
import androidx.room.Relation

data class QuietWindowWithNotifications(
    @Embedded
    val quietWindow: QuietWindowEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "quiet_window_id"
    )
    val notifications: List<NotificationEntity>
)

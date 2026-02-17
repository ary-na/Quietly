package com.any.quietly.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = QuietWindowEntity::class,
            parentColumns = ["id"],
            childColumns = ["quiet_window_id"],
            onDelete = ForeignKey.CASCADE // If a QuietWindow is deleted, its notifications are also deleted
        )
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notificationId: Int,
    val packageName: String,
    val title: String?,
    val text: String?,
    val postTime: Long,
    val tag: String? = null,
    @ColumnInfo(name = "quiet_window_id", index = true)
    val quietWindowId: Int? = null // Nullable if a notification doesn't belong to any window
)

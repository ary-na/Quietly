package com.any.quietly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notificationId: Int,
    val packageName: String,
    val title: String?,
    val text: String?,
    val postTime: Long,
    val tag: String? = null
)

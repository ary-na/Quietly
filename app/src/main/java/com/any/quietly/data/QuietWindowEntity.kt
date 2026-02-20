package com.any.quietly.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiet_windows")
data class QuietWindowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val notificationCount: Int,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true
)

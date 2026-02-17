package com.any.quietly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiet_windows")
data class QuietWindowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startTime: Long,
    val endTime: Long
)

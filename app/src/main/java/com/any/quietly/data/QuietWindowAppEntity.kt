package com.any.quietly.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "quiet_window_apps",
    primaryKeys = ["quiet_window_id", "package_name"],
    foreignKeys = [
        ForeignKey(
            entity = QuietWindowEntity::class,
            parentColumns = ["id"],
            childColumns = ["quiet_window_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quiet_window_id"), Index("package_name")]
)
data class QuietWindowAppEntity(
    @ColumnInfo(name = "quiet_window_id")
    val quietWindowId: Int,
    @ColumnInfo(name = "package_name")
    val packageName: String
)

package com.any.quietly.data

import androidx.room.Embedded
import androidx.room.Relation

data class QuietWindowWithApps(
    @Embedded
    val quietWindow: QuietWindowEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "quiet_window_id"
    )
    val apps: List<QuietWindowAppEntity>
)

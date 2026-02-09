package com.any.quietly.domain

import com.any.quietly.data.NotificationData

interface NotificationFilter {
    fun shouldBlock(notificationData: NotificationData): Boolean
}

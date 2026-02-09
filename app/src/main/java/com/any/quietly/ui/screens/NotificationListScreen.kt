package com.any.quietly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.any.quietly.data.NotificationData
import com.any.quietly.ui.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationListScreen(viewModel: NotificationViewModel = koinViewModel()) {
    val notifications by viewModel.loggedNotifications.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Logged Notifications")
        LazyColumn {
            items(notifications) { notification ->
                NotificationItem(notification = notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationData) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Title: ${notification.title ?: "No Title"}")
        Text(text = "Text: ${notification.text ?: "No Text"}")
        Text(text = "Package: ${notification.packageName}")
    }
}

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NotificationListScreen(viewModel: NotificationViewModel = koinViewModel()) {
    val notifications by viewModel.loggedNotifications.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Logged Notifications")
        LazyColumn {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onDeleteClick = { id -> viewModel.deleteNotification(id) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationListScreenPreview() {
    // Create a mock ViewModel for preview
    val mockNotifications = listOf(
        NotificationData(
            id = 1,
            packageName = "com.example.app1",
            notificationId = 101,
            tag = null,
            title = "App 1 Title",
            text = "App 1 Content",
            postTime = System.currentTimeMillis()
        ),
        NotificationData(
            id = 2,
            packageName = "com.example.app2",
            notificationId = 102,
            tag = "TAG_B",
            title = "App 2 Title",
            text = "App 2 Content",
            postTime = System.currentTimeMillis()
        )
    )
    val mockRepository = object : com.any.quietly.repository.NotificationRepository {
        override suspend fun saveNotification(notificationData: NotificationData) { /* Do nothing */
        }

        override fun getAllNotifications(): StateFlow<List<NotificationData>> =
            MutableStateFlow(mockNotifications)

        override suspend fun getAllNotificationsOnce(): List<NotificationData> = mockNotifications
        override suspend fun deleteNotification(id: Int) { /* Do nothing */
        }
    }
    val mockViewModel = object : NotificationViewModel(mockRepository) {
        override val loggedNotifications: StateFlow<List<NotificationData>> =
            MutableStateFlow(mockNotifications)

        override fun deleteNotification(id: Int) { /* Do nothing for preview */
        }
    }
    NotificationListScreen(viewModel = mockViewModel)
}

@Composable
fun NotificationItem(
    notification: NotificationData,
    onDeleteClick: (Int) -> Unit  // Callback to trigger delete, passing notification ID
) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Package: ${notification.packageName}")
            Text(text = "Title: ${notification.title ?: "No Title"}")
            Text(text = "Content: ${notification.text ?: "No Content"}")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onDeleteClick(notification.id) }) {
            Text("Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    val mockNotification = NotificationData(
        id = 1,
        packageName = "com.example.app",
        notificationId = 100,
        tag = "TAG_A",
        title = "Sample Title",
        text = "Sample Content",
        postTime = System.currentTimeMillis()
    )
    NotificationItem(
        notification = mockNotification,
        onDeleteClick = { /* Do nothing for preview */ }
    )
}

package com.any.quietly

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import com.any.quietly.data.NotificationData
import com.any.quietly.ui.theme.QuietlyTheme
import com.any.quietly.ui.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNotificationListenerEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        setContent {
            QuietlyTheme {
                // A surface container using the 'background' colour from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotificationListScreen()
                }
            }
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this)
            .contains(packageName)
    }
}

@Composable
fun NotificationListScreen(viewModel: NotificationViewModel = koinViewModel()) {
    val notifications by viewModel.loggedNotifications.collectAsState()

    // Assuming you have a Composable to display a single notification item
    LazyColumn {
        items(notifications) { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationData) {
    // Customize this to your needs
    Column {
        Text(text = "Package: ${notification.packageName}")
        Text(text = "Title: ${notification.title}")
        Text(text = "Content: ${notification.text}")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuietlyTheme {
        NotificationListScreen()
    }
}

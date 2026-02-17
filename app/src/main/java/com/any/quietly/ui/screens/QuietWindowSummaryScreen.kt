package com.any.quietly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.border
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import com.any.quietly.repository.NotificationRepository
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.any.quietly.domain.QuietWindowAlarmScheduler
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.res.stringResource
import com.any.quietly.R
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuietWindowSummaryScreen(
    quietWindowId: Int,
    onNavigateUp: () -> Unit,
    onNavigateHome: () -> Unit,
    repository: NotificationRepository = koinInject(),
    alarmScheduler: QuietWindowAlarmScheduler = koinInject(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val quietWindow by repository.getQuietWindow(quietWindowId).collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quietWindow?.name ?: "Quiet Window") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val window = quietWindow ?: return@IconButton
                            scope.launch {
                                alarmScheduler.cancel(window)
                                repository.deleteQuietWindow(window.id)
                                onNavigateHome()
                                launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Quiet window deleted"
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(id = R.string.delete_quiet_window_content_description)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Snackbar(
                        snackbarData = data,
                        shape = RoundedCornerShape(24.dp),
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .wrapContentWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(24.dp)
                            )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            quietWindow?.let {
                Text("Name: ${it.name}")
                Spacer(modifier = Modifier.height(8.dp))
                val startTime = formatTime(it.startTime)
                val endTime = formatTime(it.endTime)
                Text("Time: $startTime - $endTime")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Logged Notifications:")
                LazyColumn {
                    items(it.notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onDeleteClick = { notificationId ->
                                scope.launch {
                                    repository.deleteNotification(notificationId)
                                    snackbarHostState.showSnackbar(
                                        message = "Notification deleted"
                                    )
                                }
                            }
                        )
                    }
                }
            } ?: run {
                Text("Quiet Window not found.")
            }
        }
    }
}

private fun formatTime(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timeInMillis))
}

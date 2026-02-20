package com.any.quietly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import com.any.quietly.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.any.quietly.data.QuietWindow
import com.any.quietly.repository.NotificationRepository
import com.any.quietly.ui.components.CreateQuietWindowBottomSheet
import com.any.quietly.util.NotificationHelper
import com.any.quietly.data.QuietWindowWithApps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddRuleClick: () -> Unit = {},
    onQuietWindowClick: (Int) -> Unit,
    repository: NotificationRepository = koinInject(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val quietWindows by repository.getAllQuietWindows().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.main_screen_title)) }
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
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = onAddRuleClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_quiet_window_content_description)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val context = LocalContext.current
                FloatingActionButton(
                    onClick = {
                        val notificationHelper = NotificationHelper(context)
                        notificationHelper.showSummaryNotification("Test", "This is a test notification.")
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Send Test Notification"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (quietWindows.isEmpty()) {
            EmptyState(paddingValues)
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(quietWindows) { quietWindow ->
                    QuietWindowItem(
                        quietWindow = quietWindow,
                        onClick = { onQuietWindowClick(quietWindow.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuietWindowItem(quietWindow: QuietWindow, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = quietWindow.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Summarize after ${quietWindow.notificationCount} notifications")
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (quietWindow.isEnabled) "Status: Active" else "Status: Paused",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_main_empty),
                contentDescription = stringResource(id = R.string.empty_state_content_description),
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.primary
                ),
                alpha = 0.9f
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.empty_state_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 36.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
    val mockRepository = object : NotificationRepository {
        override suspend fun saveNotification(
            notificationData: com.any.quietly.data.NotificationData,
            quietWindowId: Int?
        ) {}
        override fun getAllNotifications(): Flow<List<com.any.quietly.data.NotificationData>> = flowOf(emptyList())
        override suspend fun getAllNotificationsOnce(): List<com.any.quietly.data.NotificationData> = emptyList()
        override suspend fun getNotificationsForQuietWindow(quietWindowId: Int): List<com.any.quietly.data.NotificationData> = emptyList()
        override suspend fun deleteNotification(id: Int) {}
        override suspend fun saveQuietWindow(quietWindow: QuietWindow): Long = 1L
        override suspend fun saveQuietWindowApps(quietWindowId: Int, packageNames: List<String>) {}
        override fun getAllQuietWindows(): Flow<List<QuietWindow>> = flowOf(
            listOf(
                QuietWindow(id = 1, name = "Work", notificationCount = 10),
                QuietWindow(id = 2, name = "Study", notificationCount = 5, isEnabled = false)
            )
        )
        override fun getQuietWindow(id: Int): Flow<QuietWindow?> = flowOf(null)
        override suspend fun getQuietWindowsWithApps(): List<QuietWindowWithApps> = emptyList()
        override suspend fun getNotificationCountForQuietWindow(quietWindowId: Int): Int = 0
        override suspend fun clearNotificationsForQuietWindow(quietWindowId: Int) {}
        override suspend fun setQuietWindowEnabled(id: Int, enabled: Boolean) {}
        override suspend fun deleteQuietWindow(id: Int) {}
    }
    MainScreen(onQuietWindowClick = {}, repository = mockRepository)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithSheet(onQuietWindowClick: (Int) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    MainScreen(
        onAddRuleClick = { scope.launch { sheetState.show() } },
        onQuietWindowClick = onQuietWindowClick,
        snackbarHostState = snackbarHostState
    )

    CreateQuietWindowBottomSheet(
        isVisible = sheetState.isVisible,
        onDismissRequest = { scope.launch { sheetState.hide() } },
        onSaved = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Quiet window created"
                )
            }
        }
    )
}

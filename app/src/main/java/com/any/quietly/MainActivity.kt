package com.any.quietly

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.any.quietly.ui.screens.MainScreenWithSheet
import com.any.quietly.ui.screens.QuietWindowSummaryScreen
import com.any.quietly.ui.theme.QuietlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isNotificationListenerEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
        requestPostNotificationsIfNeeded()
        setContent {
            QuietlyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreenWithSheet(
                                onQuietWindowClick = { quietWindowId ->
                                    navController.navigate("summary/$quietWindowId")
                                }
                            )
                        }
                        composable("summary/{quietWindowId}") { backStackEntry ->
                            val quietWindowId =
                                backStackEntry.arguments?.getString("quietWindowId")?.toInt()
                            requireNotNull(quietWindowId) { "quietWindowId parameter not found" }
                            QuietWindowSummaryScreen(
                                quietWindowId = quietWindowId,
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateHome = {
                                    navController.popBackStack("main", inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this)
            .contains(packageName)
    }

    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_POST_NOTIFICATIONS
                )
            }
        }
    }

    companion object {
        private const val REQUEST_POST_NOTIFICATIONS = 1001
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuietlyTheme {
        MainScreenWithSheet(onQuietWindowClick = {})
    }
}

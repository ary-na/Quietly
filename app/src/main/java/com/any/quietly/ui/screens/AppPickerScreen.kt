package com.any.quietly.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.any.quietly.ui.model.AppInfo
import com.any.quietly.ui.util.getInstalledApps
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.tooling.preview.Preview
import android.graphics.Bitmap
import androidx.compose.ui.res.stringResource
import com.any.quietly.R
import androidx.core.graphics.createBitmap
import androidx.compose.ui.platform.LocalResources


@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    size: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (checked) MaterialTheme.colorScheme.primary else Color.Gray)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Box(
                modifier = Modifier
                    .size(size * 0.5f)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundCheckboxPreview() {
    RoundCheckbox(checked = true, onCheckedChange = {})
}

@Composable
fun AppRow(
    app: AppInfo,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {onCheckedChange(!isChecked)}
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = app.icon.toBitmap().asImageBitmap(),
            contentDescription = app.appName,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.appName,
            modifier = Modifier.weight(1f)
        )

        RoundCheckbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppRowPreview() {
    val mockBitmap = createBitmap(1, 1)
    // Use the KTX extension function `toDrawable`
    val mockDrawable = mockBitmap.toDrawable(LocalResources.current)
    val mockAppInfo = AppInfo("com.example.app", "Mock App", mockDrawable)

    AppRow(
        app = mockAppInfo,
        isChecked = true,
        onCheckedChange = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerScreen(
    onSave: (selectedApps: Map<String, Boolean>) -> Unit = {}
) {
    val context = LocalContext.current
    val allApps = remember { getInstalledApps(context) }

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedApps = remember { mutableStateMapOf<String, Boolean>() }

    val filteredApps = if (searchQuery.isBlank()) {
        allApps
    } else {
        allApps.filter {
            it.appName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.select_apps_to_block_title)) },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSearchActive) searchQuery = ""
                            isSearchActive = !isSearchActive
                        }
                    ) {
                        Icon(
                            imageVector = if (isSearchActive)
                                Icons.Filled.Close
                            else
                                Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search_content_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 🔍 Animated Search Bar
            AnimatedVisibility(
                visible = isSearchActive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(id = R.string.search_apps_placeholder)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50),


                    )
            }

            // 📱 App list
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredApps) { app ->
                    AppRow(
                        app = app,
                        isChecked = selectedApps[app.packageName] == true,
                        onCheckedChange = { checked ->
                            selectedApps[app.packageName] = checked
                        }
                    )
                }
            }

            // 💾 Save button
            Button(
                onClick = { onSave(selectedApps) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedApps.any { it.value }
            ) {
                Text(stringResource(id = R.string.save_button_text))
            }
            if (selectedApps.none { it.value }) {
                Text(
                    text = "Select at least one app to save.",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppPickerScreenPreview() {
    AppPickerScreen()
}

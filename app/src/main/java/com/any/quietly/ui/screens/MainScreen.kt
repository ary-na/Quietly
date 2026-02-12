package com.any.quietly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import com.any.quietly.R
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import com.any.quietly.ui.components.CreateQuietWindowBottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddRuleClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.main_screen_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRuleClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_quiet_window_content_description)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🪟 Empty state icon
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

                // 📝 Empty state text
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
    MainScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MainScreenWithSheetPreview() {
    MainScreenWithSheet()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithSheet() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    MainScreen(
        onAddRuleClick = { scope.launch { sheetState.show() } }
    )

    CreateQuietWindowBottomSheet(
        isVisible = sheetState.isVisible,
        onDismissRequest = { scope.launch { sheetState.hide() } },
        onSaveClick = { scope.launch { sheetState.hide() } }
    )
}

package com.any.quietly.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.any.quietly.ui.screens.AppPickerScreen
import com.any.quietly.ui.viewmodel.QuietWindowViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuietWindowBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: QuietWindowViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
        ) {
            when (uiState.currentStep) {
                1 -> NameStep(
                    onNextClick = { name ->
                        viewModel.setName(name)
                        viewModel.nextStep()
                    }
                )
                2 -> NotificationCountStep(
                    onNextClick = { count ->
                        viewModel.setNotificationCount(count)
                        viewModel.nextStep()
                    },
                    onBackClick = { viewModel.previousStep() }
                )
                3 -> AppPickerScreen( // Reusing AppPickerScreen
                    onSave = { selectedApps ->
                        scope.launch {
                            val selectedPackages = selectedApps
                                .filter { it.value }
                                .keys
                                .toSet()
                            viewModel.setSelectedApps(selectedPackages)
                            viewModel.saveQuietWindow()
                            onSaved()
                            onDismissRequest() // Close the sheet after saving
                        }
                    }
                )
            }
        }
    }
}

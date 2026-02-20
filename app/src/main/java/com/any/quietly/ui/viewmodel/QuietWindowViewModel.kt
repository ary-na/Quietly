package com.any.quietly.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.any.quietly.data.QuietWindow
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

data class CreateQuietWindowState(
    val currentStep: Int = 1,
    val name: String = "",
    val notificationCount: Int = 10,
    val selectedApps: Set<String> = emptySet()
)

class QuietWindowViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuietWindowState())
    val uiState: StateFlow<CreateQuietWindowState> = _uiState.asStateFlow()

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }



    fun setNotificationCount(count: Int) {
        _uiState.update { it.copy(notificationCount = count) }
    }

    fun setSelectedApps(packageNames: Set<String>) {
        _uiState.update { it.copy(selectedApps = packageNames) }
    }

    fun nextStep() {
        _uiState.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun previousStep() {
        _uiState.update { it.copy(currentStep = it.currentStep - 1) }
    }

    suspend fun saveQuietWindow() {
        // Persist the rule and selected apps off the main thread.
        withContext(Dispatchers.IO) {
            val currentState = _uiState.value
            val quietWindow = QuietWindow(
                name = currentState.name,
                notificationCount = currentState.notificationCount
            )
            val newId = repository.saveQuietWindow(quietWindow)
            repository.saveQuietWindowApps(newId.toInt(), currentState.selectedApps.toList())
        }

        // Once the background work is done, reset the state on the main thread.
        _uiState.value = CreateQuietWindowState()
    }
}

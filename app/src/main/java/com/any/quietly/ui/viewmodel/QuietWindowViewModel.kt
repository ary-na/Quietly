package com.any.quietly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.any.quietly.data.QuietWindow
import com.any.quietly.domain.QuietWindowAlarmScheduler
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CreateQuietWindowState(
    val currentStep: Int = 1,
    val name: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val selectedApps: Set<String> = emptySet()
)

class QuietWindowViewModel(
    private val repository: NotificationRepository,
    private val alarmScheduler: QuietWindowAlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuietWindowState())
    val uiState: StateFlow<CreateQuietWindowState> = _uiState.asStateFlow()

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }



    fun setTime(startTime: Long, endTime: Long) {
        _uiState.update { it.copy(startTime = startTime, endTime = endTime) }
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
        // Perform the database and alarm scheduling on the IO dispatcher
        withContext(Dispatchers.IO) {
            val currentState = _uiState.value
            var quietWindow = QuietWindow(
                name = currentState.name,
                startTime = currentState.startTime,
                endTime = currentState.endTime
            )
            val newId = repository.saveQuietWindow(quietWindow)
            quietWindow = quietWindow.copy(id = newId.toInt())
            repository.saveQuietWindowApps(newId.toInt(), currentState.selectedApps.toList())
            alarmScheduler.schedule(quietWindow)
        }

        // Once the background work is done, reset the state on the main thread.
        _uiState.value = CreateQuietWindowState()
    }
}

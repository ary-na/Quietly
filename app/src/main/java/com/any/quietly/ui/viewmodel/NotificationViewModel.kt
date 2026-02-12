package com.any.quietly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.any.quietly.data.NotificationData
import com.any.quietly.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

open class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    private val _loggedNotifications = MutableStateFlow<List<NotificationData>>(emptyList())
    open val loggedNotifications: StateFlow<List<NotificationData>> = _loggedNotifications.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        repository.getAllNotifications()
            .onEach { notifications ->
                _loggedNotifications.value = notifications
            }
            .catch { e ->
                // Handle error, e.g., log it or show a message to the user
                println("Error loading notifications: $e")
            }
            .launchIn(viewModelScope) // Collect the flow within the ViewModel's scope
    }

    // This method might be used if you need to manually add a notification (e.e., from a direct user action)
    // For notifications received by the service, the service itself logs them, and the ViewModel observes the DB.
    fun addNotification(notificationData: NotificationData) {
        viewModelScope.launch {
            try {
                repository.saveNotification(notificationData)
                // No need to reload manually, the Flow from the repository will update the StateFlow
            } catch (e: Exception) {
                println("Error adding notification: $e")
            }
        }
    }

    open fun deleteNotification(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(id)
                // The Flow from the repository will emit an updated list automatically
            } catch (e: Exception) {
                println("Error deleting notification: $e")
            }
        }
    }
}

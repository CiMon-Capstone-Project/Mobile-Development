package com.example.cimon_chilimonitoring.ui.tracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo
import kotlinx.coroutines.launch

class TrackingViewModel(application: Application, private val repository: TrackingRepo) : AndroidViewModel(application) {
    fun addTracking(trackingEntity: TrackingEntity) {
        viewModelScope.launch() {
            repository.insertReminder(trackingEntity)
        }
    }

    fun getAllReminders() = repository.getAllReminders()

    fun updateTracking(trackingEntity: TrackingEntity) {
        viewModelScope.launch() {
            repository.updateReminder(trackingEntity)
        }
    }

    fun deleteReminder(trackingEntity: TrackingEntity) {
        viewModelScope.launch() {
            repository.deleteReminder(trackingEntity)
        }
    }
}
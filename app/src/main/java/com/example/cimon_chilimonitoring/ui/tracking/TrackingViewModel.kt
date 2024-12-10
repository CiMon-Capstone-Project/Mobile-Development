package com.example.cimon_chilimonitoring.ui.tracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo
import com.example.cimon_chilimonitoring.data.local.repository.UserRepository
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class TrackingViewModel(application: Application, private val repository: TrackingRepo) : AndroidViewModel(application) {

//    private val repository: TrackingRepo
//    val getAllTracking: LiveData<List<TrackingEntity>>

//    init {
//        val trackingDao = HistoryDatabase.getInstance(application).trackingDao()
//        repository = TrackingRepo(trackingDao)
//
//        getAllTracking = repository.getAllTracking
//    }

    fun addTracking(trackingEntity: TrackingEntity) {
        viewModelScope.launch() {
            repository.insertReminder(trackingEntity)
        }
    }

    fun getAllReminders() = repository.getAllReminders()

//    fun getAllReminders() = repository.getAllTracking()
//
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
//
//    fun deleteTracking(trackingEntity: TrackingEntity) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteTracking(trackingEntity)
//        }
//    }
//
//    fun deleteAllTracking() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteAllTracking()
//        }
//    }

}
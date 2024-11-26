package com.example.cimon_chilimonitoring.ui.detection.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.helper.di.AppExecutors

class HistoryViewModel(private val eventRepo: HistoryRepo) : ViewModel()   {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _favoriteEventsEntity = MutableLiveData<List<HistoryEntity>>()
    val favoriteEventsEntity: LiveData<List<HistoryEntity>> = _favoriteEventsEntity

    init {
        observeHistory()
    }

    private fun observeHistory() {
        _isLoading.value = true
        eventRepo.getHistory().observeForever { events ->
            _isLoading.value = false
            _favoriteEventsEntity.value = events
        }
    }
}
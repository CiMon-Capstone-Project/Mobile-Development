package com.example.cimon_chilimonitoring.ui.detection.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemHistory

class HistoryViewModel(private val eventRepo: HistoryRepo) : ViewModel()   {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _favoriteEventsEntity = MutableLiveData<List<HistoryEntity>>()
    val favoriteEventsEntity: LiveData<List<HistoryEntity>> = _favoriteEventsEntity

    private val _listStory = MutableLiveData<List<ResultsItemHistory>?>()
    val listStory: LiveData<List<ResultsItemHistory>?> = _listStory

    private var page = 1

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

    suspend fun detectPages(token: String) {
        while (true) {
            try {
                val response = ApiConfig.getApiService(token).getHistory(page)
                if (response.data?.results?.isNotEmpty() == true) {
                    page++
                } else {
                    break
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    suspend fun getHistory(token:String){
        _isLoading.value = true
        try {
            for (i in 1..<page) {
                val response = ApiConfig.getApiService(token).getHistory(i)
                _listStory.value = response.data?.results?.filterNotNull()
            }
        } catch (e: Exception) {
            _listStory.value = null
        } finally {
            _isLoading.value = false
        }
    }
}
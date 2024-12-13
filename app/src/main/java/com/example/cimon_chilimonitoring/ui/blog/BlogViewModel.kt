package com.example.cimon_chilimonitoring.ui.blog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItemBlog

class BlogViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ResultsItemBlog>?>()
    val listStory: LiveData<List<ResultsItemBlog>?> = _listStory

    suspend fun getStory(token: String) {
        if (_listStory.value != null) return

        _isLoading.value = true
        try {
            val response = ApiConfig.getApiService(token).getBlog()
            _listStory.value = response.data?.results?.filterNotNull()
        } catch (e: Exception) {
            _listStory.value = null
        } finally {
            _isLoading.value = false
        }
    }
}
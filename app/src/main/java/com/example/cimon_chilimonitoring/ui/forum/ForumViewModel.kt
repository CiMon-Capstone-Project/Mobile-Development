package com.example.cimon_chilimonitoring.ui.forum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cimon_chilimonitoring.data.remote.response.ListStory
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItem
import com.example.cimon_chilimonitoring.data.remote.retrofit.ApiService
import kotlinx.coroutines.launch

class ForumViewModel() : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ResultsItem>?>()
    val listStory: LiveData<List<ResultsItem>?> = _listStory

    // new
    suspend fun getStory(token: String) {
        if (_listStory.value != null) return

        _isLoading.value = true
        try {
            val response = ApiConfig.getApiService(token).getArticles()
            _listStory.value = response.data?.results?.filterNotNull()
            Log.d("ForumViewModel", "getStory: ${response.data?.results}")
        } catch (e: Exception) {
            _listStory.value = null
        } finally {
            _isLoading.value = false
        }
    }

    fun refreshStories(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService(token).getArticles()
                _listStory.value = response.data?.results?.filterNotNull()
                Log.d("ForumViewModel", "refreshStories: ${response.data?.results}")
            } catch (e: Exception) {
                _listStory.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // delete
    fun deleteArticle(token: String, id: Int) {
        viewModelScope.launch {
            runCatching {
                ApiConfig.getApiService(token).deleteArticle(id)
            }.onSuccess {
                _listStory.value = _listStory.value?.filterNot { it.id == id }
            }.onFailure {
                Log.e("ForumViewModel", "Failed to delete article", it)
            }
        }
    }

    // get articles by Id
    suspend fun getFilteredStory(token: String, userId: String) {
        if (_listStory.value != null) return

        _isLoading.value = true
        try {
            val response = ApiConfig.getApiService(token).getArticles()
            _listStory.value = response.data?.results?.filterNotNull()?.filter { it.userId == userId }
            Log.d("ForumViewModel", "getFilteredStory: ${_listStory.value}")
        } catch (e: Exception) {
            _listStory.value = null
        } finally {
            _isLoading.value = false
        }
    }
}
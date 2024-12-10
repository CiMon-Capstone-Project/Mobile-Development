package com.example.cimon_chilimonitoring.ui.result

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.remote.response.Treatments
import okhttp3.MediaType
import okhttp3.RequestBody

class ResultViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listTreatments = MutableLiveData<List<Treatments>?>()
    val listTreatments: LiveData<List<Treatments>?> = _listTreatments

    suspend fun getTreatments(token: String, id: Int) {
        if (_listTreatments.value != null) return
        _isLoading.value = true
        try {
            val response = ApiConfig.getApiService(token).getTreatments(id)
            _listTreatments.value = response.data?.treatments?.let { listOf(it) }
            Log.d("ResultViewModel", "getTreatments: ${response.data?.treatments}")
        } catch (e: Exception) {
            _listTreatments.value = null
        } finally {
            _isLoading.value = false
        }
    }
}
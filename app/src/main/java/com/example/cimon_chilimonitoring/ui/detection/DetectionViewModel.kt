package com.example.cimon_chilimonitoring.ui.detection

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.remote.response.Treatments

class DetectionViewModel : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listTreatments = MutableLiveData<List<Treatments>?>()
    val listTreatments: LiveData<List<Treatments>?> = _listTreatments

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    suspend fun getTreatments(token: String, id: Int) {
        if (_listTreatments.value != null) return
        _isLoading.value = true
        try {
            val response = ApiConfig.getApiService(token).getTreatments(id)
            _listTreatments.value = response.data?.treatments?.let { listOf(it) }
        } catch (e: Exception) {
            _listTreatments.value = null
        } finally {
            _isLoading.value = false
        }
    }
}
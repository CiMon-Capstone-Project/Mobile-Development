package com.example.cimon_chilimonitoring.ui.forum.addPost

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cimon_chilimonitoring.data.local.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AddPostViewModel: ViewModel(){
//    suspend fun getToken(): String {
//        return repository.getSession().map { it.token }.first()
//    }

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
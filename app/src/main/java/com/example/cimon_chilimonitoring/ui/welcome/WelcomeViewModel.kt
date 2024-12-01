package com.example.cimon_chilimonitoring.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.cimon_chilimonitoring.data.local.pref.UserModel
import com.example.cimon_chilimonitoring.data.local.repository.UserRepository

class WelcomeViewModel (private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
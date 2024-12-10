package com.example.cimon_chilimonitoring.data.local.pref

import androidx.lifecycle.MutableLiveData

object TokenManager {
    val idTokenLiveData = MutableLiveData<String?>()
    val userIdLiveData = MutableLiveData<String?>()
    val emailLiveData = MutableLiveData<String?>()

    var idToken: String?
        get() = idTokenLiveData.value
        set(value) {
            idTokenLiveData.postValue(value)
        }

    var userId: String?
        get() = userIdLiveData.value
        set(value) {
            userIdLiveData.postValue(value)
        }

    var email: String?
        get() = emailLiveData.value
        set(value) {
            emailLiveData.postValue(value)
        }
}
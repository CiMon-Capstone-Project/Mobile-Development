package com.example.cimon_chilimonitoring.helper.di

import android.content.Context
import com.example.cimon_chilimonitoring.data.local.pref.UserPreference
import com.example.cimon_chilimonitoring.data.local.pref.dataStore
import com.example.cimon_chilimonitoring.data.local.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}
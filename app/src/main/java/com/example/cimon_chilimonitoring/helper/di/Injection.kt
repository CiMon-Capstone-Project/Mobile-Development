package com.example.cimon_chilimonitoring.helper.di

import android.content.Context
import com.example.cimon_chilimonitoring.data.local.pref.UserPreference
import com.example.cimon_chilimonitoring.data.local.pref.dataStore
import com.example.cimon_chilimonitoring.data.local.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)

//        val user = runBlocking { pref.getSession().first() }
//        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref)
    }
//
//    fun provideRepositoryStory(context: Context): StoryRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getSession().first() }
//        val apiService = ApiConfig.getApiService(user.token)
//        return StoryRepository.getInstance(apiService, pref)
//    }
//
//    fun provideRepositoryStoryID(context: Context): StoryIDRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val user = runBlocking { pref.getSession().first() }
//        val apiService = ApiConfig.getApiService(user.token)
//        return StoryIDRepository.getInstance(apiService, pref)
//    }
}
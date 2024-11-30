package com.example.cimon_chilimonitoring.data.local.repository

import com.example.cimon_chilimonitoring.data.local.pref.UserModel
import com.example.cimon_chilimonitoring.data.local.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
//    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

//    suspend fun register(name: String, email: String, password: String): RegisterResponse {
//        return apiService.register(name, email, password)
//    }
//
//    suspend fun login(email: String, password: String): LoginResponse {
//        return apiService.login(email, password)
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
//            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
//                instance ?: UserRepository(userPreference, apiService)
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}
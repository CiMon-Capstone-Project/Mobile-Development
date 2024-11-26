package com.example.cimon_chilimonitoring.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.room.HistoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HistoryRepo  private constructor(
    private val historyDao: HistoryDao,
) {

    // MediatorLiveData digunakan untuk menggabungkan dua sumber data (LiveData) yang berbeda
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // insert history
    suspend fun insertHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    fun saveHistoryToDatabase(history: List<HistoryEntity>) {
        coroutineScope.launch {
            history.forEach { insertHistory(it) }
        }
    }
    companion object {
        @Volatile
        private var instance: HistoryRepo? = null

        fun getInstance(historyDao: HistoryDao): HistoryRepo =
            instance ?: synchronized(this) {
                instance ?: HistoryRepo(historyDao).also { instance = it }
            }
    }

    // history
    fun getHistory() : LiveData<List<HistoryEntity>> {
        Log.d("HistoryRepo", "Fetching history from database")
        return historyDao.getHistory()
    }
}
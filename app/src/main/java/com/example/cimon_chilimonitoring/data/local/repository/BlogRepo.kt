package com.example.cimon_chilimonitoring.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.cimon_chilimonitoring.data.local.entity.BlogEntity
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.room.blog.BlogDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BlogRepo  private constructor(
    private val blogDao: BlogDao,
) {

    // MediatorLiveData digunakan untuk menggabungkan dua sumber data (LiveData) yang berbeda
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // insert history
    suspend fun insertBlog(history: BlogEntity) {
        blogDao.insertBlog(history)
    }

    fun saveHistoryToDatabase(history: List<BlogEntity>) {
        coroutineScope.launch {
            history.forEach { insertBlog(it) }
        }
    }
    companion object {
        @Volatile
        private var instance: BlogRepo? = null

        fun getInstance(blogDao: BlogDao): BlogRepo =
            instance ?: synchronized(this) {
                instance ?: BlogRepo(blogDao).also { instance = it }
            }
    }

    // history
    fun getHistory() : LiveData<List<BlogEntity>> {
        Log.d("HistoryRepo", "Fetching history from database")
        return blogDao.getBlog()
    }
}
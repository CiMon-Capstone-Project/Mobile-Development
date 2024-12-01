package com.example.cimon_chilimonitoring.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cimon_chilimonitoring.data.local.repository.HistoryRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.helper.di.AppExecutors
import com.example.cimon_chilimonitoring.ui.detection.history.HistoryViewModel
import com.example.cimon_chilimonitoring.ui.forum.addPost.AddPostViewModel

class ViewModelFactory private constructor(private val historyRepo: HistoryRepo) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> historyViewModel as T
            modelClass.isAssignableFrom(AddPostViewModel::class.java) -> AddPostViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    val appExecutors = AppExecutors()
    private val historyViewModel = HistoryViewModel(historyRepo)

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(HistoryRepo.getInstance(HistoryDatabase.getInstance(context).historyDao()))
            }.also { instance = it }
    }
}
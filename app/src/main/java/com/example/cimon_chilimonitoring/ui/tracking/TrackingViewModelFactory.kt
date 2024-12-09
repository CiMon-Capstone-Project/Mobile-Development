package com.example.cimon_chilimonitoring.ui.tracking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo

class TrackingViewModelFactory(val app: Application, private val reminderRepository: TrackingRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrackingViewModel(app, reminderRepository) as T
    }
}
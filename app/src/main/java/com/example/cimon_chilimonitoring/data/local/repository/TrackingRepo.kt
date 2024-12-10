package com.example.cimon_chilimonitoring.data.local.repository

import androidx.lifecycle.LiveData
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.data.local.room.tracking.TrackingDao

class TrackingRepo(private val db: HistoryDatabase) {
    fun getAllReminders() = db.trackingDao().getAllTracking()

    suspend fun insertReminder(reminder: TrackingEntity) {
        db.trackingDao().addTracking(reminder)
    }

    suspend fun updateReminder(reminder: TrackingEntity) {
        db.trackingDao().updateTrackingById(
            id = reminder.id,
            title = reminder.title,
            description = reminder.description,
            startTime = reminder.startTime
        )
    }

    suspend fun deleteReminder(reminder: TrackingEntity) {
        db.trackingDao().deleteTracking(reminder)
    }
}
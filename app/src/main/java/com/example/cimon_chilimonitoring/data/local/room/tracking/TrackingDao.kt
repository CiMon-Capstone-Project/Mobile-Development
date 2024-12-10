package com.example.cimon_chilimonitoring.data.local.room.tracking

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity

@Dao
interface TrackingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTracking(tracking: TrackingEntity)

    @Query("UPDATE tracking SET title = :title, description = :description, startTime = :startTime WHERE id = :id")
    suspend fun updateTrackingById(id: Int, title: String, description: String, startTime: String)

    @Delete
    suspend fun deleteTracking(tracking: TrackingEntity)

    @Query ("SELECT * FROM tracking ORDER BY id DESC")
    fun getAllTracking(): LiveData<List<TrackingEntity>>

    @Query ("DELETE FROM tracking")
    suspend fun deleteAllTracking()

    @Query("DELETE FROM tracking WHERE id = :id")
    suspend fun deleteTrackingById(id: Int)
}
package com.example.cimon_chilimonitoring.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: HistoryEntity)

    // suspend function untuk mendukung Coroutines
    @Update
    suspend fun updateNews(news: HistoryEntity)

    // history
    @Query("SELECT * FROM history")
    fun getHistory() : LiveData<List<HistoryEntity>>
}
package com.example.cimon_chilimonitoring.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cimon_chilimonitoring.data.local.entity.BlogEntity
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.room.blog.BlogDao
import com.example.cimon_chilimonitoring.data.local.room.tracking.TrackingDao

@Database(entities = [HistoryEntity::class, BlogEntity::class, TrackingEntity::class], version = 3, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao
    abstract fun blogDao(): BlogDao
    abstract fun trackingDao(): TrackingDao

    companion object {
        @Volatile
        private var instance: HistoryDatabase? = null
        fun getInstance(context: Context): HistoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java, "offline_support.db"
                ).build()
            }
    }
}
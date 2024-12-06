package com.example.cimon_chilimonitoring.data.local.room.blog

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cimon_chilimonitoring.data.local.entity.BlogEntity

@Database(entities = [BlogEntity::class], version = 2, exportSchema = false)
abstract class BlogDatabase : RoomDatabase(){
    abstract fun blogDao(): BlogDao

    companion object {
        @Volatile
        private var instance: BlogDatabase? = null
        fun getInstance(context: Context): BlogDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BlogDatabase::class.java, "history.db"
                ).build()
            }
    }
}
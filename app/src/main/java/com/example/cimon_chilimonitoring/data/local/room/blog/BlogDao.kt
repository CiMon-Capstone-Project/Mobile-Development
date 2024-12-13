package com.example.cimon_chilimonitoring.data.local.room.blog

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cimon_chilimonitoring.data.local.entity.BlogEntity
import com.example.cimon_chilimonitoring.data.local.entity.HistoryEntity

@Dao
interface BlogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBlog(blog: BlogEntity)

    @Update
    suspend fun updateNews(news: BlogEntity)

    // history
    @Query("SELECT * FROM blog")
    fun getBlog() : LiveData<List<BlogEntity>>
}
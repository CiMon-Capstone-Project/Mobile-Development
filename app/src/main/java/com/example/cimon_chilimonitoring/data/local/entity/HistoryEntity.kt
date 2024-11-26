package com.example.cimon_chilimonitoring.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "history")
data class HistoryEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @field:ColumnInfo(name = "uriImage")
    val uriImage: String,

    @field:ColumnInfo(name = "result")
    val result: String? = null,

    @field:ColumnInfo(name = "detail")
    val detail: String? = null,

    @field:ColumnInfo(name = "analyzeTime")
    val analyzeTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()
    )
)
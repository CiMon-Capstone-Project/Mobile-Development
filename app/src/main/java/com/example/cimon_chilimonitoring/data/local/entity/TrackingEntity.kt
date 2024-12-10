package com.example.cimon_chilimonitoring.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "tracking")
data class TrackingEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @field:ColumnInfo(name = "title")
    val title: String,

    @field:ColumnInfo(name = "description")
    val description: String,

    @field:ColumnInfo(name = "startTime")
    val startTime: String,

) : Parcelable

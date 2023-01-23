package com.pakollya.paginglist.data.cache.day

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parts")
data class DayPart(
    @PrimaryKey(autoGenerate = true)
    val index: Int = 0,
    val pageIndex: Int,
    val startId: Long,
    val endId: Long,
    val reminder: Boolean,
    val startPosition: Int,
    val endPosition: Int,
    val date: Long,
)
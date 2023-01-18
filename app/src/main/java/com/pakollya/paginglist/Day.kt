package com.pakollya.paginglist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "days")
data class Day(
    @PrimaryKey
    val index: Int,
    val size: Int,
    val startPosition: Int,
    val date: Long
)
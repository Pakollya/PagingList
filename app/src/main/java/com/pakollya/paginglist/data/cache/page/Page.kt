package com.pakollya.paginglist.data.cache.page

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class Page(
    @PrimaryKey
    val index: Int,
    val startId: Long,
    val endId: Long,
)
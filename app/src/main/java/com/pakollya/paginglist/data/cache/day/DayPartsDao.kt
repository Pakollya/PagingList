package com.pakollya.paginglist.data.cache.day

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DayPartsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayParts(days: List<DayPart>)

    @Query("SELECT * FROM parts WHERE startId <= :id AND endId >= :id")
    fun dayPartById(id: Long): DayPart

    @Query("DELETE FROM parts WHERE pageIndex = :index")
    fun deletePartsByPage(index: Int)

    @Query("DELETE FROM parts")
    fun deleteDayParts()
}
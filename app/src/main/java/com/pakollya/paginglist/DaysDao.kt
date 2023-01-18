package com.pakollya.paginglist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDays(days: List<Day>)

    @Query("SELECT * FROM days ORDER BY `index`")
    fun days(): List<Day>

    @Query("SELECT date FROM days WHERE `index` LIKE :index")
    fun dayByIndex(index: Int): Long

    @Query("SELECT `index` FROM days WHERE date LIKE :date")
    fun indexByDate(date: Long): Int

    @Query("SELECT COUNT(`index`) FROM days")
    fun daysCount(): Int
}
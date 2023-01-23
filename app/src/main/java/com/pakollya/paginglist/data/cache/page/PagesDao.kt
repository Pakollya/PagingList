package com.pakollya.paginglist.data.cache.page

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPages(pages: List<Page>)

    @Query("SELECT `index` FROM pages WHERE startId <= :id AND endId >= :id")
    fun pageIndexById(id: Long): Int

    @Query("DELETE FROM pages")
    fun deletePages()
}
package com.pakollya.paginglist.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pakollya.paginglist.data.cache.day.DayPart
import com.pakollya.paginglist.data.cache.day.DayPartsDao
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao
import com.pakollya.paginglist.data.cache.page.Page
import com.pakollya.paginglist.data.cache.page.PagesDao

@Database(
    entities = [
        Message.Data::class,
        DayPart::class,
        Page::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao

    abstract fun dayPartsDao(): DayPartsDao

    abstract fun pagesDao(): PagesDao
}
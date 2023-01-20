package com.pakollya.paginglist

import androidx.room.Database
import androidx.room.RoomDatabase

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
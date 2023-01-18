package com.pakollya.paginglist

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Message.Data::class,
        Day::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao

    abstract fun daysDao(): DaysDao
}
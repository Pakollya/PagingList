package com.pakollya.paginglist

import android.content.Context
import androidx.room.Room

interface MessagesCache {
    fun dataBase(): MessagesDatabase

    class Cache(context: Context) : MessagesCache {
        private val database: MessagesDatabase by lazy {
            Room.databaseBuilder(
                context.applicationContext,
                MessagesDatabase::class.java,
                "messages"
            ).build()
        }

        override fun dataBase() = database
    }
}
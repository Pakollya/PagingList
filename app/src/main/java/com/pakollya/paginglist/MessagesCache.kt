package com.pakollya.paginglist

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

interface MessagesCache {
    fun dataBase(): MessagesDatabase

    class Cache(context: Context) : MessagesCache {
        private val database: MessagesDatabase by lazy {
            Room.databaseBuilder(
                context.applicationContext,
                MessagesDatabase::class.java,
                "messages"
            )
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
        }

        override fun dataBase() = database

        companion object{
            private val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE pages ('index' INTEGER PRIMARY KEY NOT NULL, startId INTEGER NOT NULL DEFAULT 0, endId INTEGER NOT NULL DEFAULT 0)")
                    database.execSQL("CREATE TABLE parts ('index' INTEGER PRIMARY KEY NOT NULL, pageIndex INTEGER NOT NULL DEFAULT 0, startId INTEGER NOT NULL DEFAULT 0, endId INTEGER NOT NULL DEFAULT 0, reminder INTEGER NOT NULL DEFAULT 0, startPosition INTEGER NOT NULL DEFAULT 0, endPosition INTEGER NOT NULL DEFAULT 0, date INTEGER NOT NULL DEFAULT 0)")
                }
            }

            private val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("DROP TABLE days")
                }
            }
        }
    }
}
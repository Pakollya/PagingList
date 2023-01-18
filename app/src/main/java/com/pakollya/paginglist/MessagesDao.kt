package com.pakollya.paginglist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(messages: List<Message.Data>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMessage(messages: Message.Data)

    @Query("SELECT timestamp FROM messages WHERE id LIKE :id")
    fun dateById(id: Long): Long

    @Query("SELECT * FROM messages ORDER BY timestamp")
    fun messages(): List<Message.Data>

    @Query("SELECT * FROM messages WHERE timestamp >= :startDate AND timestamp < :endDate  ORDER BY timestamp")
    fun messages(startDate: Long, endDate: Long): List<Message.Data>

    @Query("SELECT MAX(id) FROM messages")
    fun lastId(): Long
}
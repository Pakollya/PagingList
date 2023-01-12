package com.pakollya.paginglist

import java.text.SimpleDateFormat
import java.util.*

interface MessageCache {

    fun init()

    fun messages(): List<Message.Data>

    fun addMessage()

    object Cache: MessageCache {
        private var listMessage = mutableListOf<Message.Data>()
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        override fun messages(): List<Message.Data> = listMessage

        override fun addMessage() {
            val newId = listMessage.last().id + 1
            listMessage.add(Message.Data(newId, "message $newId", date()))
        }

        override fun init() {
            listMessage.clear()
            for (i in 0..300L) {
                listMessage.add(Message.Data(i, "message $i", date()))
            }
        }

        private fun date(): String {
            return dateFormat.format(Date()).toString()
        }

    }

}
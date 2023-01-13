package com.pakollya.paginglist

interface MessageCache {

    fun init()

    fun messages(): List<Message.Data>

    fun addMessage()

    fun count(): Int

    class Base : MessageCache {
        private var listMessage = mutableListOf<Message.Data>()

        override fun messages(): List<Message.Data> = listMessage

        override fun addMessage() {
            val newId = listMessage.last().id + 1
            listMessage.add(Message.Data(newId, "message $newId", date()))
        }

        override fun init() {
            listMessage.clear()
            for (i in 0..350L) {
                listMessage.add(Message.Data(i, "message $i", date()))
            }
        }

        override fun count() = listMessage.count()

        private fun date(): Long = System.currentTimeMillis()
    }
}
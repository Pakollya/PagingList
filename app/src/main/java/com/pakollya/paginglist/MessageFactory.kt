package com.pakollya.paginglist

import android.util.Log

interface MessageFactory {

    fun messages(): List<Message.Data>

    class Base : MessageFactory {

        override fun messages(): List<Message.Data> {
            val listMessage = mutableListOf<Message.Data>()

            for(i in 0..2) {
                val tempDate = System.currentTimeMillis() - (1000*60*60*24 * i)
                var id: Long

                when(i) {
                    0 -> for (k in 0..50) {
                        id = k*10 + 10000L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                        if (k == 50)
                            Log.e("LastId", "$id")
                    }

                    1 -> for (k in 0..30) {
                        id = k*5 + 1000L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }

                    2 -> for (k in 0..30) {
                        id = k + 100L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }
                }
            }

            return listMessage
        }
    }
}
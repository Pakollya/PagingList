package com.pakollya.paginglist

interface MessageFactory {

    fun messages(): List<Message.Data>

    class Base : MessageFactory {

        override fun messages(): List<Message.Data> {
            val listMessage = mutableListOf<Message.Data>()

            for(i in 0..4) {
                val tempDate = System.currentTimeMillis() - (1000*60*60*24 * i)
                var id: Long

                when(i) {
                    0 -> for (k in 1..100) {
                        id = k*10 + 20000L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }

                    1 -> for (k in 1..50) {
                        id = k*10 + 9000L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }

                    2 -> for (k in 1..70) {
                        id = k*5 + 1000L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }

                    3 -> for (k in 1..50) {
                        id = k*2 + 500L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }
                    4 -> for (k in 1..10) {
                        id = k*3 + 10L
                        listMessage.add(Message.Data(id, "message $id", tempDate))
                    }
                }
            }

            return listMessage
        }
    }
}
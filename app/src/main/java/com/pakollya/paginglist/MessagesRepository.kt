package com.pakollya.paginglist

import com.pakollya.paginglist.MessagesRepository.Strategy.NEXT
import com.pakollya.paginglist.MessagesRepository.Strategy.PREVIOUS

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    fun init()

    fun messages(strategy: Strategy): List<Message>

    fun addMessage()

    class Base: MessagesRepository {
        private var page = 0

        override fun init() {
            MessageCache.Cache.init()
        }

        override fun messages(strategy: Strategy): List<Message> {
            if (strategy == NEXT) {
                page++
            } else if (strategy == PREVIOUS) {
                page--
            }
            val allMessages = MessageCache.Cache.messages()

            val list = mutableListOf<Message>()

            if (page > 0) {
                list.add(Message.Previous)
            }

            for (i in 0 until PAGE_SIZE) {
                list.add(allMessages[(page* PAGE_SIZE) + i])
            }

            if (page + 1 < MAXIMUM_PAGES) {
                list.add(Message.Next)
            }

            return list
        }

        override fun addMessage() {
            MessageCache.Cache.addMessage()
        }

        companion object {
            private const val PAGE_SIZE = 100
            private const val MAXIMUM_PAGES = 3
        }
    }
}
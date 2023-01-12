package com.pakollya.paginglist

import android.util.Log
import com.pakollya.paginglist.MessagesRepository.Strategy.*

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    fun init()

    fun messages(strategy: Strategy = INIT): List<Message>

    fun changePage(id: Int): Boolean

    fun positionOnPageById(id: Int): Int

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

        override fun changePage(id: Int): Boolean {
            val itemPage = id/PAGE_SIZE
            Log.d("OldPage ", "$page")
            Log.d("NewPage ", "$itemPage")

            return if (itemPage == page){
                false
            } else {
                page = itemPage
                true
            }
        }

        override fun positionOnPageById(id: Int): Int {
            Log.d("id ", "$id")
            Log.d("page*PAGE_SIZE ", "${page*PAGE_SIZE}")
            Log.d("position ", "${id - page*PAGE_SIZE + 1}")
            return id - page*PAGE_SIZE + 1
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
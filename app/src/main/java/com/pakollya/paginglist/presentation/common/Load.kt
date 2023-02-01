package com.pakollya.paginglist.presentation.common

import com.pakollya.paginglist.data.MessagesRepository.Strategy
import com.pakollya.paginglist.data.MessagesRepository.Strategy.INIT
import com.pakollya.paginglist.data.cache.message.Message

interface Load : LoadMessages, MapIsLoading, IsLoading
interface LoadMessages{
    fun loadMessages(strategy: Strategy = INIT, messages: List<Message.Data> = emptyList())
}

interface MapIsLoading {
    fun mapIsLoading(isLoading: Boolean)
}

interface IsLoading {
    fun isLoading(): Boolean
}
package com.pakollya.paginglist.presentation

import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.MessagesRepository

data class MessagesPageUi(
    val messages: List<Message>,
    val pageIndex: Int,
    val pageSize:Int,
    val strategy: MessagesRepository.Strategy
)
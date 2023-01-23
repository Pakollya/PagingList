package com.pakollya.paginglist

data class MessagesPageUi(
    val messages: List<Message>,
    val pageIndex: Int,
    val pageSize:Int,
    val strategy: MessagesRepository.Strategy
)
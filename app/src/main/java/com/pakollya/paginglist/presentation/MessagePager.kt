package com.pakollya.paginglist.presentation

import androidx.paging.*
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.cache.message.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MessagePager<T> {

    fun messagePagingData(scope: CoroutineScope, initialPage: Int = 0): Flow<T>

    class Pager(
        private val repository: MessagesRepository,
    ) : MessagePager<PagingData<Message>> {

        override fun messagePagingData(scope: CoroutineScope, initialPage: Int): Flow<PagingData<Message>> {
            return Pager(
                config = PagingConfig(
                    pageSize = 100,
                    prefetchDistance = 20,
                    enablePlaceholders = false
                ),
                initialKey = initialPage,
                repository.messagesDataSource().asPagingSourceFactory(Dispatchers.IO)
            )
                .flow
                .cachedIn(scope)
                .map {
                it.insertSeparators { first: Message?, second: Message? ->
                    if (second == null)
                        return@insertSeparators null

                    if (first is Message.Header || second is Message.Header)
                        return@insertSeparators null

                    return@insertSeparators if (first == null || first.day() != second.day())
                        Message.Header(second.day())
                    else
                        null
                }
            }
        }
    }
}
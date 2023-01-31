package com.pakollya.paginglist.data

import androidx.paging.DataSource
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MessagesRepository {

    suspend fun init()
    suspend fun updatePages()
    suspend fun addMessage()
    suspend fun positionOnPageById(id: Int): Int
    fun messagesDataSource(): DataSource.Factory<Int, Message.Data>
    fun messagesFlow(): Flow<List<Message.Data>>

    class Base(
        private val dao: MessagesDao,
        private val pagesRepository: PagesRepository,
        private val factory: MessageFactory,
    ) : MessagesRepository {

        private val mutex = Mutex()

        override fun messagesDataSource() = dao.messagesDataSource()

        override fun messagesFlow() = dao.messagesFlow()

        override suspend fun init() = mutex.withLock {
            dao.delete()
            dao.insertMessages(factory.messages())
        }

        override suspend fun updatePages() = mutex.withLock {
            //Получаем все сообщения
            val allMessages = dao.messages()

            //вычисляем количество страниц и сохраняем
            pagesRepository.updatePageCount(allMessages.size)

            //парсим все сообщения на страницы
            pagesRepository.parseAllMessages(allMessages)
        }

        /**
         * Вычисляем позицию элемента в списке по id (для RecyclerView)
         **/
        override suspend fun positionOnPageById(id: Int): Int = mutex.withLock {
            val dayPart = pagesRepository.dayPartById(id.toLong())
            val messages = dao.messagesById(dayPart.startId, dayPart.endId)

            var position = 0

            if (messages.isNotEmpty()) {
                messages.forEachIndexed { index, data ->
                    if (data.messageId() == id.toLong()) {
                        position = (dayPart.startPosition + if(index > 2) (index - 2) else index)
                        return@forEachIndexed
                    }
                }
            }

            return position
        }

        override suspend fun addMessage() = mutex.withLock {
            val now = System.currentTimeMillis()
            val lastId = dao.lastId()

            dao.addMessage(Message.Data(
                lastId + 10,
                "message ${lastId + 10}",
                now
            ))
        }
    }
}
package com.pakollya.paginglist.data

import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MessagesRepository {

    suspend fun init()
    suspend fun updatePages()
    suspend fun messagesByPageIndex(pageIndex: Int): List<Message>
    suspend fun addMessage()
    suspend fun pageIndexById(id: Int): Int
    suspend fun positionOnPageById(id: Int): Int
    fun isLastPage(index: Int): Boolean

    class Base(
        private val dao: MessagesDao,
        private val pagesRepository: PagesRepository,
        private val factory: MessageFactory,
    ) : MessagesRepository {

        private val mutex = Mutex()

        override suspend fun init() = mutex.withLock {
            dao.delete()
            dao.insertMessages(factory.messages())
        }

        override suspend fun updatePages() {
            //Получаем все сообщения
            val allMessages = dao.messages()

            //вычисляем количество страниц и сохраняем
            pagesRepository.updatePageCount(allMessages.size)

            //парсим все сообщения на страницы
            pagesRepository.parseAllMessages(allMessages)
        }

        override suspend fun messagesByPageIndex(pageIndex: Int): List<Message> = mutex.withLock {
            updatePages()

            val messages = messagesByPage(
                pageIndex = pageIndex
            )

            return messages
        }

        override suspend fun pageIndexById(id: Int): Int = mutex.withLock {
            pagesRepository.pageIndexById(id.toLong())
        }

        suspend fun messagesByPage(pageIndex: Int): List<Message> {
            val page = pagesRepository.pageByIndex(pageIndex)
            val messages = dao.messagesById(page.startId, page.endId)

            return messages
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
                        position = dayPart.startPosition + index
                        return@forEachIndexed
                    }
                }
            }

            return position
        }

        override suspend fun addMessage() {
            val now = System.currentTimeMillis()
            val lastId = dao.lastId()

            dao.addMessage(Message.Data(
                lastId + 10,
                "message ${lastId + 10}",
                now
            ))
        }

        override fun isLastPage(index: Int) = pagesRepository.isLastPage(index)
    }
}
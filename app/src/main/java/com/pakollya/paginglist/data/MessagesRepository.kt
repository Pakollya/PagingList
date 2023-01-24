package com.pakollya.paginglist.data

import com.pakollya.paginglist.presentation.MessagesPageUi
import com.pakollya.paginglist.data.MessagesRepository.Strategy.*
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    suspend fun init()
    suspend fun messages(strategy: Strategy = INIT): MessagesPageUi
    suspend fun changePage(id: Int): Boolean
    suspend fun positionOnPageById(id: Int): Int
    suspend fun addMessage()
    suspend fun setLastPage()
    suspend fun lastPosition(): Int
    fun isLastPage(): Boolean
    fun isFirstPage(): Boolean

    class Base(
        private val dao: MessagesDao,
        private val pagesRepository: PagesRepository,
        private val factory: MessageFactory
    ) : MessagesRepository {

        override suspend fun init() {
            dao.delete()
            dao.insertMessages(factory.messages())
        }

        /**
         * Получаем индекс стриницы и количество стриниц
         * отдаем список сообщений для данной стриницы на UI
         **/
        suspend fun messagesByPage(pageIndex: Int, pageCount: Int): List<Message> {
            //создаем новый список для отображения
            val list = mutableListOf<Message>()

            //Получаем все кусочки дня, находящиеся на странице
            val dayParts = pagesRepository.dayPartsByPageIndex(pageIndex)

            dayParts.forEach { day ->
                val tempMessages = dao.messagesById(day.startId, day.endId)

                if (tempMessages.isNotEmpty()) {
                    //Добавляем Header если это начало дня, а НЕ остаток дня
                    if (!day.reminder) {
                        list.add(Message.Header(pagesRepository.dateStringFromMillis(day.date)))
                    }
                    list.addAll(tempMessages)
                }
            }

            return list
        }

        /**
         * Отдаем список сообщений для одной страницы в зависимости от стратегии
         **/
        override suspend fun messages(strategy: Strategy): MessagesPageUi {
            //Получаем все сообщения
            val allMessages = dao.messages()

            //вычисляем количество страниц и сохраняем
            pagesRepository.updatePageCount(allMessages.size)

            //парсим все сообщения на страницы, а также на дни внутри страницы
            pagesRepository.parseAllMessages(allMessages)

            val pageIndex = pagesRepository.pageByStrategy(strategy)

            val messages = messagesByPage(
                pageIndex = pageIndex,
                pageCount = pagesRepository.pageCount()
            )

            val messagesUi = MessagesPageUi(
                messages = messages,
                pageIndex = pageIndex,
                pageSize = messages.size,
                strategy = strategy
            )

            pagesRepository.updateCurrentPageUi(
                page = messagesUi,
                strategy = strategy
            )

            return messagesUi
        }

        /**
         * Меняем значение страницы на новый по id элемента
         **/
        override suspend fun changePage(id: Int): Boolean {
            //Находим индекс страницы по id элемента
            val pageIndex = pagesRepository.pageIndexById(id.toLong())

            //проверяем находимся ли мы сейчас на данной странице или на другой
            return pagesRepository.updatePage(pageIndex)
        }

        override suspend fun setLastPage() {
            pagesRepository.setLastPage()
        }

        /**
         * Вычисляем позицию элемента в списке по id (для RecyclerView)
         **/
        override suspend fun positionOnPageById(id: Int): Int {
            val dayPart = pagesRepository.dayPartById(id.toLong())
            val messages = dao.messagesById(dayPart.startId, dayPart.endId)

            var position = 0
            if (messages.isNotEmpty()) {
                messages.forEachIndexed { index, data ->
                    if (data.messageId() == id.toLong()) {
                        position = dayPart.startPosition + index
                    }
                }
            }

            //если мы уже находимся на странице, то проверяем вторая ли она (есть ли перед ней страница, чтобы скорректировать position)
            val pages = pagesRepository.currentPagesUi()

            if (pages.isNotEmpty() && pages.size >= 2 && pages[1].pageIndex == dayPart.pageIndex) {
                position += pages[0].pageSize
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

        override suspend fun lastPosition(): Int {
            val lastId = dao.lastId()
            return pagesRepository.lastPositionById(lastId)
        }

        override fun isLastPage() = pagesRepository.isLastPage()

        override fun isFirstPage() = pagesRepository.isFirstPage()
    }
}
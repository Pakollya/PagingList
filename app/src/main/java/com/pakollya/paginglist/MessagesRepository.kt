package com.pakollya.paginglist

import android.util.Log
import com.pakollya.paginglist.MessagesRepository.Strategy.*

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    suspend fun init()
    suspend fun messages(strategy: Strategy = INIT): List<Message>
    suspend fun changePage(id: Int): Boolean
    suspend fun positionOnPageById(id: Int): Int
    suspend fun addMessage()
    suspend fun setLastPage()
    suspend fun lastPosition(): Int

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

            //если НЕ первая страница, то добавляем возможность вернуться на предыдущую страницу
            if (pageIndex > 0) {
                list.add(Message.Previous)
            }

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

            //если НЕ последняя страница, то добавляем возможность перейти на следующую страницу
            if (pageIndex < pageCount) {
                list.add(Message.Next)
            }

            return list
        }

        /**
         * Отдаем список сообщений для одной страницы в зависимости от стратегии
         **/
        override suspend fun messages(strategy: Strategy): List<Message> {
            pagesRepository.updateStrategy(strategy)

            Log.e("messages", "${pagesRepository.currentPage()}")

            //Получаем все сообщения
            val allMessages = dao.messages()

            //вычисляем количество страниц и сохраняем
            pagesRepository.updatePageCount(allMessages.size)

            //парсим все сообщения на страницы, а также на дни внутри страницы
            pagesRepository.parseAllMessages(
                messages = allMessages,
                pageCount = pagesRepository.pageCount(),
                pageSize = pagesRepository.pageSize()
            )

            return messagesByPage(
                pageIndex = pagesRepository.currentPage(),
                pageCount = pagesRepository.pageCount()
            )
        }

        /**
         * Меняем значение страницы на новый по id элемента
         **/
        override suspend fun changePage(id: Int): Boolean {
            //Находим индекс страницы по id элемента
            val pageIndex = pagesRepository.pageIndexById(id.toLong())

            Log.e("changePage pageIndex", "$pageIndex")

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

            if (messages.isNotEmpty()) {
                messages.forEachIndexed { index, data ->
                    if (data.messageId() == id.toLong()) {
                        //TODO:check
                        val position = dayPart.startPosition + index
                        return position
                    }
                }
            }

            return 0
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
    }
}
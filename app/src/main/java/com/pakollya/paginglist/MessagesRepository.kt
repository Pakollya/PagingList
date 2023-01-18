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
    suspend fun addMessage(): Int
    suspend fun setLastPage()

    class Base(
        private val dao: MessagesDao,
        private val daysRepository: DaysRepository,
        private val factory: MessageFactory
    ): MessagesRepository {
        private val messageList = mutableListOf<Message>()
        private var page = 0

        override suspend fun init() {
            dao.insertMessages(factory.messages())
        }

        /**
         * Отдаем список сообщений для одной страницы в зависимости от стратегии
         **/
        override suspend fun messages(strategy: Strategy): List<Message> {
            //Стратегия: двигаемся на страницу вперед или назад
            if (strategy == NEXT) {
                page++
            } else if (strategy == PREVIOUS) {
                page--
            }

            //Получаем все сообщения и создаем новый список для отображения
            val allMessages = dao.messages()
            if (allMessages.isNotEmpty()) {
                daysRepository.parseMessages(allMessages)
            }

            val list = mutableListOf<Message>()

            //если НЕ первая страница, то добавляем возможность вернуться на предыдущую страницу
            if (page > 0) {
                list.add(Message.Previous)
            }

            //Добавляем Header
            list.add(Message.Header(
                daysRepository.dateStringByIndex(page)
            ))

            val date = daysRepository.dateMillisByIndex(page)
            //Добавляем в список сообщения для страницы page
            list.addAll(
                allMessages.filter { date <= it.timestamp &&
                        it.timestamp < (date + daysRepository.dayMillis())
                }
            )

            //если НЕ последняя страница, то добавляем возможность перейти на следующую страницу
            if (page + 1 < daysRepository.daysCount()) {
                list.add(Message.Next)
            }

            if (list.isNotEmpty()) {
                messageList.clear()
                messageList.addAll(list)
            }

            return list
        }

        suspend fun dateById(id: Long): Long {
            return daysRepository.dateToStartDay(dao.dateById(id))
        }

        /**
         * Меняем значение страницы на новый по id элемента
         **/
        override suspend fun changePage(id: Int): Boolean {
            //Находим индекс страницы по дате
            val date = dateById(id.toLong())
            val itemPage = daysRepository.indexByDate(date)

            Log.d("ItemPage ", "$itemPage")

            //проверяем находимся ли мы сейчас на данной странице или на другой
            return if (itemPage == page){
                false
            } else {
                page = itemPage
                true
            }
        }

        override suspend fun setLastPage() {
            //TODO: check
            page = daysRepository.daysCount() - 1
        }

        /**
         * Вычисляем позицию элемента в списке по id (для RecyclerView)
         **/
        override suspend fun positionOnPageById(id: Int): Int {
            Log.d("id ", "$id")
            val date = dateById(id.toLong())
            val messages = dao.messages(date, date + daysRepository.dayMillis())

            if (messages.isNotEmpty()) {
                messages.forEachIndexed { index, data ->
                    if (data.messageId() == id.toLong()) {
                        //TODO:check
                        Log.e("positionOnPageById", "$index")
                        return index + 1
                    }
                }
            }

            return 0
        }

        override suspend fun addMessage(): Int {
            val now = System.currentTimeMillis()
            val lastId = dao.lastId()
            dao.addMessage(Message.Data(
                lastId + 10,
                "message ${lastId + 10}",
                now
            ))
            val nowStartDate = daysRepository.dateToStartDay(now)
            val messagesCount = dao
                .messages(
                    nowStartDate,
                    nowStartDate + daysRepository.dayMillis()
                )
                .count()

            return messagesCount + 1
        }
    }
}
package com.pakollya.paginglist.data

import com.pakollya.paginglist.presentation.PageUi
import com.pakollya.paginglist.data.MessagesRepository.Strategy.*
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.cache.message.MessagesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MessagesRepository {

    enum class Strategy {
        INIT,
        NEXT,
        PREVIOUS
    }

    suspend fun init()
    fun messagesFlow(): Flow<List<Message.Data>>
    suspend fun updatePages(messages: List<Message.Data> = emptyList())
    suspend fun updateMessages(strategy: Strategy = INIT): List<PageUi>

    suspend fun changePage(id: Int): Boolean
    suspend fun positionOnPageById(id: Int): Int
    suspend fun addMessage()

    fun isLastPage(): Boolean
    fun isFirstPage(): Boolean

    class Base(
        private val dao: MessagesDao,
        private val pagesRepository: PagesRepository,
        private val factory: MessageFactory
    ) : MessagesRepository {

        private val mutex = Mutex()

        override suspend fun init() = mutex.withLock {
            dao.delete()
            dao.insertMessages(factory.messages())
        }

        override fun messagesFlow() = dao.messagesFlow()

        override suspend fun updatePages(messages: List<Message.Data>) = mutex.withLock {
            //Получаем все сообщения
            val allMessages = messages.ifEmpty { dao.messages() }

            //вычисляем количество страниц и сохраняем
            pagesRepository.updatePageCount(allMessages.size)

            //парсим все сообщения на страницы, а также на дни внутри страницы
            pagesRepository.parseAllMessages(allMessages)
        }

        /**
         * Получаем стратегию и отдаем список страниц в зависимости от стратегии
         **/
        override suspend fun updateMessages(strategy: Strategy): List<PageUi> = mutex.withLock {
            //Обновляем список страниц в зависимости от стратегии
            pagesRepository.updateCurrentPages(strategy)

            //получаем список актуальных страниц
            val pages = pagesRepository.currentPages()

            val uiList = mutableListOf<PageUi>()
            var list: List<Message>

            //пробегаемся по списку страниц и создаем PageUi для каждой страницы
            pages.forEach { page ->
                //получаем список сообщений для данной страницы
                list = messagesByPage(page)
                if (list.isNotEmpty()) {
                    val ui = PageUi(
                        list,
                        page,
                        list.size,
                        INIT
                    )

                    uiList.add(ui)
                }
            }

            //обновляем и сохраняем список актуальных PageUi
            pagesRepository.updateCurrentPageUi(uiList)

            return uiList
        }


        /**
         * Получаем индекс стриницы
         * отдаем список сообщений для данной стриницы
         **/
        suspend fun messagesByPage(pageIndex: Int): List<Message> {
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
         * Меняем значение страницы на новый по id элемента
         **/
        override suspend fun changePage(id: Int): Boolean  = mutex.withLock {
            //Находим индекс страницы по id элемента
            val pageIndex = pagesRepository.pageIndexById(id.toLong())

            //проверяем количество элементов на стринице
            //если элеметов мало, то нужно подгрузить дополнительно предыдущую страницу
            val messagesCount = messagesByPage(pageIndex).size

            //проверяем находимся ли мы сейчас на данной странице или на другой
            return pagesRepository.updatePage(pageIndex, messagesCount > 20)
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
                    }
                }
            }

            //если мы уже находимся на странице, то проверяем вторая ли она (есть ли перед ней страница, чтобы скорректировать position)
            val pages = pagesRepository.currentPages()

            if (pages.isNotEmpty() && pages.size >= 2 && pages.last() == dayPart.pageIndex) {
                pages.forEach {
                    if (it == dayPart.pageIndex)
                        return@forEach

                    val messagesCount = messagesByPage(it).count()
                    position += messagesCount
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

        override fun isLastPage() = pagesRepository.isLastPage()

        override fun isFirstPage() = pagesRepository.isFirstPage()
    }
}